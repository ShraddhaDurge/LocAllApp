package com.localapp.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.localapp.model.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.localapp.model.Product;
import com.localapp.model.ProductCategoryTags;
import com.localapp.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    BusinessService businessService;

    @Autowired
    ProductRepository productRepository;


    public Product saveVendorProduct(Product product, int business_id) {
        Set<ProductCategoryTags> productTags = product.getProductTags();

        product.setProductTags(productTags);

        product.setBusiness(businessService.getById(business_id));

        productRepository.save(product);
        return product;
    }


    public List<Product> getBusinessProducts(int businessId) {
        // TODO Auto-generated method stub
        return productRepository.findByBusiness(businessService.getById(businessId));

    }

    public Product updateVendorProduct(Product product) {
        Product updateProduct = productRepository.findById(product.getProductId());
        Set<ProductCategoryTags> productTags = product.getProductTags();
        updateProduct.setProductName(product.getProductName());
        updateProduct.setProductTags(productTags);
        updateProduct.setQuantAvailable(product.getQuantAvailable());
        updateProduct.setPrice(product.getPrice());
        updateProduct.setProductDesc(product.getProductDesc());
        productRepository.save(product);

        return product;

    }
    public void saveProduct(Product product) {

        productRepository.save(product);
    }

    public Product getById(int productId) {
        return productRepository.findById(productId);
    }

    public Product deleteById(int productId) {
        return productRepository.deleteById(productId);
    }

    public Boolean existsById(int productId) {
        return productRepository.existsById(productId);
    }

    public void uploadProductImage(int productId, byte[] imageBytes) throws IOException {
        Product product = getById(productId);
        System.out.println(product + "  "+imageBytes);
        String encodedImage = Base64Utils.encodeToString(imageBytes);
        product.setProductImage(encodedImage);
        saveProduct(product);
    }


}