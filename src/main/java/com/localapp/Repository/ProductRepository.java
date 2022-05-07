package com.localapp.Repository;


import com.localapp.Model.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.localapp.Model.Product;

import java.util.List;


@Repository("productRepository")
public interface ProductRepository extends JpaRepository<Product, Integer>{
    Product findById(int productId);
    Boolean existsById(int productId);
    Product deleteById(int productId);
    List<Product> findAll();

    Product findByProductName(String productName);
}