package com.jyj.inventorysystemforconcurrency.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private long quantity;

    public Stock(final long productId, final long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(final long amount) {
        checkEnoughQuantity(amount);
        quantity -= amount;
    }

    public void checkEnoughQuantity(final long amount) {
        if (quantity <= 0) {
            throw new RuntimeException("재고가 없습니다.");
        }

        if (quantity < amount) {
            throw new RuntimeException("재고가 부족합니다.");
        }
    }
}
