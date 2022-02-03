package com.localapp.PayloadResponse;

import com.localapp.model.Product;

import lombok.Data;

@Data
public class ProductRegResponse {

    private String message;
    private Product product;

    public ProductRegResponse(String message, Product product) {
        this.message = message;
        this.product = product;
    }

}