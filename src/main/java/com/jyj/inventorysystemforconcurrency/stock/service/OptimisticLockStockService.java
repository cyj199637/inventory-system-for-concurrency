package com.jyj.inventorysystemforconcurrency.stock.service;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import com.jyj.inventorysystemforconcurrency.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(final long id, final long amount) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id).orElseThrow();
        stock.decrease(amount);
    }
}
