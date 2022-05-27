package com.localapp.PayloadResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorAnalyticsResponse {
    int totalSales;
    double revenue;
    int totalStocks;
    int totalBuyers;
    List<ProductWiseSales> productWiseSales;
    List<MonthWiseRevenue> monthWiseRevenue;
    List<VendorTopProducts> vendorTopProducts;
    List<VendorTopProducts> vendorLastProducts;
}
