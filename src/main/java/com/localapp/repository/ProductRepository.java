package com.localapp.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.localapp.model.Product;


@Repository("productRepository")
public interface ProductRepository extends JpaRepository<Product, Integer>{
    Product findById(int productId);
    Product findByProductName(String productName);
    Boolean existsById(int productId);
    Product deleteById(int productId);

}