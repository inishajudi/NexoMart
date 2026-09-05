package com.nexo.nexomart.dto;

import java.math.BigDecimal;

/**
 * Search/filter parameters for F3 (Week 5 polish: price range + sort added on top of
 * Week 2's keyword/category). Built via the fluent setters below rather than a
 * telescoping constructor.
 */
public class ProductSearchCriteria {
    private String keyword;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private SortBy sortBy = SortBy.NEWEST;

    public enum SortBy { NEWEST, PRICE_ASC, PRICE_DESC, NAME_ASC }

    public static ProductSearchCriteria empty() {
        return new ProductSearchCriteria();
    }

    public String getKeyword() { return keyword; }
    public ProductSearchCriteria keyword(String keyword) { this.keyword = keyword; return this; }

    public String getCategory() { return category; }
    public ProductSearchCriteria category(String category) { this.category = category; return this; }

    public BigDecimal getMinPrice() { return minPrice; }
    public ProductSearchCriteria minPrice(BigDecimal minPrice) { this.minPrice = minPrice; return this; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public ProductSearchCriteria maxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; return this; }

    public SortBy getSortBy() { return sortBy; }
    public ProductSearchCriteria sortBy(SortBy sortBy) {
        this.sortBy = sortBy != null ? sortBy : SortBy.NEWEST;
        return this;
    }
}
