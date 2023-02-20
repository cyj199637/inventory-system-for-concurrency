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

    // @Transactional 어노테이션이 빠지게 되면 getLock() 과 releaseLock() 의 커넥션이 달라지게 되므로 주의
    @Transactional
    public void decrease(final long id, final long quantity) {
        String key = String.valueOf(id);

        /*
            named lock 같은 경우에는 다른 세션이 락을 점유 중이라면
            주어진 시간 동안 기다리면서 락 획득을 시도하기 때문에 별도의 retry 로직을 구현할 필요가 없다.
         */
        try {
            lockRepository.getLock(key);
            stockService.decreaseWithNamedLock(id, quantity);
        } finally {
            lockRepository.releaseLock(key);
        }
    }
}
