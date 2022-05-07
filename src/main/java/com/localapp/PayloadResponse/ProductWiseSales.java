package com.localapp.PayloadResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWiseSales {
    String product;
    int sales;
    double revenue;

}
