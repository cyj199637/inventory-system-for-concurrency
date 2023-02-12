package com.jyj.inventorysystemforconcurrency.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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

        assertThat(stockService.findById(1L).getQuantity()).isEqualTo(48L);
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
        assertThatThrownBy(() -> stockService.decrease(1L, 100L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("재고가 부족합니다.");
    }
}