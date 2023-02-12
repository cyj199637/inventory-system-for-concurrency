package com.jyj.inventorysystemforconcurrency.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
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
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Test
    @DisplayName("재고 감소 성공")
    void decrease() {
        stockService.decrease(1L, 2L);

        assertThat(stockService.findById(1L).getQuantity()).isEqualTo(98L);
    }

    @Test
    @DisplayName("현재 재고가 0보다 작으면 재고 감소하는데 실패한다.")
    void fail_to_decrease_with_zero_stock() {
        assertThatThrownBy(() -> stockService.decrease(2L, 2L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("재고가 없습니다.");
    }

    @Test
    @DisplayName("현재 재고가 입력 값보다 적으면 재고 감소하는데 실패한다.")
    void fail_to_decrease_with_current_stock() {
        assertThatThrownBy(() -> stockService.decrease(1L, 200L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @Test
    void 동시에_100개_요청이_들어오는_경우() throws InterruptedException {
        int totalThreadCount = 100;

        // ExecutorService: 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 자바 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // CountDownLatch: 다른 스레드에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch countDownLatch = new CountDownLatch(totalThreadCount);

        for (int i = 0; i < totalThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        // 레이스 컨디션이 발생하여 테스트 실패
        assertThat(stockService.findById(1L).getQuantity()).isEqualTo(0);
    }
}