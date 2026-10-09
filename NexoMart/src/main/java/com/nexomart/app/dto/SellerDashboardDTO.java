package com.nexomart.app.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carries seller sales summary data to the dashboard view.
 */
public class SellerDashboardDTO {

    private int totalOrders;
    private BigDecimal totalRevenue;
    private List<ProductStat> productStats;

    public SellerDashboardDTO(int totalOrders, BigDecimal totalRevenue, List<ProductStat> productStats) {
        this.totalOrders   = totalOrders;
        this.totalRevenue  = totalRevenue;
        this.productStats  = productStats;
    }

    public int getTotalOrders()              { return totalOrders; }
    public BigDecimal getTotalRevenue()      { return totalRevenue; }
    public List<ProductStat> getProductStats() { return productStats; }

    /** Per-product breakdown row. */
    public static class ProductStat {
        private String     productName;
        private int        unitsSold;
        private BigDecimal revenue;

        public ProductStat(String productName, int unitsSold, BigDecimal revenue) {
            this.productName = productName;
            this.unitsSold   = unitsSold;
            this.revenue     = revenue;
        }

        public String     getProductName() { return productName; }
        public int        getUnitsSold()   { return unitsSold; }
        public BigDecimal getRevenue()     { return revenue; }
    }
}
