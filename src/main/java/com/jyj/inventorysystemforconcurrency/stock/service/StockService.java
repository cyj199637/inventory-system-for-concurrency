package com.jyj.inventorysystemforconcurrency.stock.service;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import com.jyj.inventorysystemforconcurrency.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public Stock findById(final long id) {
        return stockRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 재고입니다."));
    }

    @Transactional
    public void decrease(final long id, final long amount) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(amount);
    }
}
