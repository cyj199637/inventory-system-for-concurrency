package com.jyj.inventorysystemforconcurrency.stock.service.facade;

import com.jyj.inventorysystemforconcurrency.redis.RedisClient;
import com.jyj.inventorysystemforconcurrency.stock.service.StockService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonLockStockServiceFacade {

    private static final long DEFAULT_WAIT_TIME = 5L;
    private static final long DEFAULT_LEASE_TIME = 1L;

    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decrease(final long id, final long amount) throws InterruptedException {
        String key = String.valueOf(id);

        RLock lock = redissonClient.getLock(key);

        try {
            boolean available = lock.tryLock(DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("fail to get lock");
                return;
            }

            stockService.decrease(id, amount);
        } finally {
            lock.unlock();
        }
    }
}
