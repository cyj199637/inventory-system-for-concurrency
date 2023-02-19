package com.jyj.inventorysystemforconcurrency.stock.service.facade;

import com.jyj.inventorysystemforconcurrency.redis.RedisClient;
import com.jyj.inventorysystemforconcurrency.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockStockServiceFacade {

    private final RedisClient redisClient;
    private final StockService stockService;

    public void decrease(final long id, final long amount) throws InterruptedException {
        while (!redisClient.lock(id)) {
            // 스레드의 락 획득 재시도 간 텀을 두기 위한 용도
            Thread.sleep(100);
        }

        try {
            stockService.decrease(id, amount);
        } finally {
            redisClient.unlock(id);
        }
    }
}
