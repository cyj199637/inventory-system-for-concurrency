package com.jyj.inventorysystemforconcurrency.stock.service.facade;

import com.jyj.inventorysystemforconcurrency.stock.service.StockServiceWithOptimisticLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockServiceFacade {

    private final StockServiceWithOptimisticLock stockService;

    public void decrease(final long id, final long quantity) throws InterruptedException {
        while (true) {
            try {
                stockService.decrease(id, quantity);

                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
