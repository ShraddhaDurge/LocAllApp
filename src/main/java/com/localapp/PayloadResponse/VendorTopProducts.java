package com.localapp.PayloadResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorTopProducts {
    String productName;
    double totalRevenue;
    int totalSales;
    int inventory;
}

