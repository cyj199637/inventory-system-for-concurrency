package com.jyj.inventorysystemforconcurrency.domain.repository;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
