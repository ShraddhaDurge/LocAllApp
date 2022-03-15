package com.localapp.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.localapp.Model.*;
import com.localapp.Repository.CategoryTagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.localapp.Repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    BusinessService businessService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryTagsRepository categoryTagsRepository;

    public Product saveVendorProduct(Product product, int business_id) {
//        product.setProductTags(product.getProductTags());

        Business b = businessService.getById(business_id);
        Set<Product> products = b.getProducts();
        products.add(product);
        productRepository.save(product);
        businessService.saveBusiness(b);

        return product;
    }


    public Set<Product> getBusinessProducts(int businessId) {
        return businessService.getById(businessId).getProducts();

    }

    public Product updateVendorProduct(Product product) {
        Product updateProduct = productRepository.findById(product.getProductId());
        Set<ProductCategoryTags> productTags = product.getProductTags();
        updateProduct.setProductName(product.getProductName());
        updateProduct.setProductTags(productTags);
        updateProduct.setQuantAvailable(product.getQuantAvailable());
        updateProduct.setPrice(product.getPrice());
        updateProduct.setProductDesc(product.getProductDesc());

        if(product.getProductImage() != null)
            updateProduct.setProductImage(product.getProductImage());

        productRepository.save(updateProduct);

        return updateProduct;

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
        String encodedImage = Base64Utils.encodeToString(imageBytes);
        product.setProductImage(encodedImage);
        saveProduct(product);
    }

    public List<CategoryTags> getAllTags() {
        return categoryTagsRepository.findAll();
    }

    public List<Product> getMostPopularProducts()
    {
        List<Product> products = productRepository.findAll();
        List<Product> popularProducts = products.stream()
                .sorted(Comparator.comparing(Product::getTotalSales).reversed())
                .collect(Collectors.toList());

        List<Product> mostPopularProducts=new ArrayList<>();

        if(popularProducts!=null && popularProducts.size()>10)
        {
            for(int i=0;i<10;i++)
            {
                mostPopularProducts.add(popularProducts.get(i));
            }
        }
        else
        {
            mostPopularProducts = popularProducts;
        }
        return mostPopularProducts;
    }
}
