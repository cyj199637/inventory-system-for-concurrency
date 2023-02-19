package com.jyj.inventorysystemforconcurrency.stock.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import com.jyj.inventorysystemforconcurrency.domain.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.jdbc.SqlGroup;

@SqlGroup(
    {
        @Sql(scripts = "/sql/init.sql",
            executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(transactionMode = TransactionMode.ISOLATED)),
        @Sql(scripts = "/sql/clean.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(transactionMode = TransactionMode.ISOLATED)
        ),
    }
)
@SpringBootTest
class LettuceLockStockServiceFacadeTest {

    @Autowired
    private LettuceLockStockServiceFacade lettuceLockStockServiceFacade;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void decrease() throws InterruptedException {
        int totalThreadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(totalThreadCount);

        for (int i = 0; i < totalThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockServiceFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(0);
    }
}