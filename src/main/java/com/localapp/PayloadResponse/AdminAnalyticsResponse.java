package com.localapp.PayloadResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAnalyticsResponse {
    int totalSales;
    double revenue;
    int totalCustomers;
    int totalVendors;
    int totalProducts;
    int totalCategories;
    List<AdminCategorySales> categorySales;
    List<MonthWiseRevenue> monthWiseRevenues;
    List<VendorTopProducts> topProducts;
    List<VendorTopProducts> leastSellingProducts;
}
