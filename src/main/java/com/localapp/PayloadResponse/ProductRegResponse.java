package com.localapp.PayloadResponse;

import com.localapp.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRegResponse {

    private String message;
    private Product product;
}