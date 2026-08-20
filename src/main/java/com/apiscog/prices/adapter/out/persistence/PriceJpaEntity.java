package com.apiscog.prices.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prices")
public class PriceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private long brandId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "price_list", nullable = false)
    private long priceList;

    @Column(name = "product_id", nullable = false)
    private long productId;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "curr", nullable = false, length = 3)
    private String currency;

    protected PriceJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public long getBrandId() {
        return brandId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public long getPriceList() {
        return priceList;
    }

    public long getProductId() {
        return productId;
    }

    public int getPriority() {
        return priority;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
