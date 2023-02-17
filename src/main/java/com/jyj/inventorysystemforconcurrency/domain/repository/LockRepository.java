package com.jyj.inventorysystemforconcurrency.domain.repository;

import com.jyj.inventorysystemforconcurrency.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
    예제에서는 편의상 JpaRepository native query를 사용했지만
    실무에서는 별도의 JDBC를 사용해야 함

    또한, 예제에서는 편의상 같은 DataSource를 사용했지만 실무에서는 Named Lock 전용 DataSource를 분리해야 함
    -> Named Lock은 락 획득에 필요한 connection과 transaction(로직)에 필요한 connection 1개, 총 2개의 connection을 사용하기 때문
      또한, 락을 해제하지 않을 수도 있는 상황까지 생긴다면 커넥션 풀이 부족해질 수 있어 다른 로직에도 영향이 가게 됨
 */
public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
