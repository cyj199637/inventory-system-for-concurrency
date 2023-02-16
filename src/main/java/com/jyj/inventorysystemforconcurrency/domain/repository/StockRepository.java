package com.jyj.inventorysystemforconcurrency.domain.repository;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id = :id")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})
    Optional<Stock> findByIdWithPessimisticLock(Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.id = :id")
    Optional<Stock> findByIdWithOptimisticLock(Long id);
}
