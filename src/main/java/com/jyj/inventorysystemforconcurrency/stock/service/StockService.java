package com.jyj.inventorysystemforconcurrency.stock.service;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import com.jyj.inventorysystemforconcurrency.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public synchronized void decrease(final long id, final long amount) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(amount);
    }

    public synchronized void decreaseWithSynchronized(final long id, final long amount) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(amount);

        // @Transactional 어노테이션을 제거했으므로 repository에서 수동으로 flush 해줘야 데이터베이스에 반영됨
        stockRepository.saveAndFlush(stock);
    }

    @Transactional
    public void decreaseWithPessimisticLock(final long id, final long amount) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id).orElseThrow();
        stock.decrease(amount);
    }

    // 락을 해제하기 전에 데이터베이스에 커밋하기 위해 부모 트랜잭션과 분리
    // (부모 트랜잭션과 동일한 범위로 묶인다면 synchronized와 같은 문제 발생)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseWithNamedLock(final long id, final long amount) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(amount);
    }
}
