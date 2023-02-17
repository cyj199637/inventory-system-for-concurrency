package com.jyj.inventorysystemforconcurrency.stock.service.facade;

import com.jyj.inventorysystemforconcurrency.domain.repository.LockRepository;
import com.jyj.inventorysystemforconcurrency.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockStockServiceFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;

    @Transactional
    public void decrease(final long id, final long quantity) {
        String key = String.valueOf(id);

        try {
            lockRepository.getLock(key);
            stockService.decreaseWithNamedLock(id, quantity);
        } finally {
            lockRepository.releaseLock(key);
        }
    }
}
