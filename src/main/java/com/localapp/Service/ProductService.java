package com.localapp.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.localapp.Model.*;
import com.localapp.PayloadResponse.ProductCategoryResponse;
import com.localapp.Repository.BasketItemsRepository;
import com.localapp.Repository.ProductTagsRepository;
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
    BasketItemsRepository basketItemsRepository;

    @Autowired
    ProductTagsRepository categoryTagsRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveVendorProduct(Product product, int business_id) {
//        product.setProductTags(product.getProductTags());
        product.setRating(0);
        product.setTotalSales(0);
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
        Set<ProductTags> productTags = product.getProductTags();
        updateProduct.setProductName(product.getProductName());
        updateProduct.setProductTags(productTags);
        updateProduct.setQuantAvailable(product.getQuantAvailable());
        updateProduct.setPrice(product.getPrice());
        updateProduct.setProductDesc(product.getProductDesc());
        updateProduct.setTotalSales(product.getTotalSales());
        updateProduct.setRating(product.getRating());
        updateProduct.setMaxDiscount(product.getMaxDiscount());
        updateProduct.setMinProducts(product.getMinProducts());
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

    public Product findByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }

    public Boolean deleteById(int productId) {
        Product product = getById(productId);
        List<BasketItem> basketItems = basketItemsRepository.findByProduct(product);
        for(BasketItem basketItem : basketItems) {
            basketItemsRepository.delete(basketItem);
        }
        productRepository.deleteById(productId);
        return true;
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

    public List<ProductTags> getAllTags() {
        return categoryTagsRepository.findAll();
    }

    public List<ProductCategoryResponse> getProductCategories() {
        List<Business> businesses = businessService.findAllBusinesses();
        List<ProductCategoryResponse> productCategories = new ArrayList<>();

        for(Business b : businesses) {
            String category= b.getBusinessCategory();
            category = category.replaceAll("Store", "");
            category = category.replaceAll("Shop", "");
            category = category.trim();
            if(!b.getProducts().isEmpty()) {
                Optional<Product> p = b.getProducts().stream().findFirst();
                ProductCategoryResponse pc = new ProductCategoryResponse(category, p.get().getProductImage());
                productCategories.add(pc);
            }
        }
        return productCategories;
    }

    public List<Product> getCategoryWiseProducts(String getcategory) {
        List<Business> businesses = businessService.findAllBusinesses();
        List<Product> categoryProducts = new ArrayList<>();

        for(Business b : businesses) {
            String category= b.getBusinessCategory();
            category = category.replaceAll("Store", "");
            category = category.replaceAll("Shop", "");
            category = category.trim();
            if(category.equalsIgnoreCase(getcategory)) {
                if (!b.getProducts().isEmpty()) {
                    for (Product p : b.getProducts()) {
                        categoryProducts.add(p);
                    }
                }
            }
        }
        return categoryProducts;
    }

    public List<Product> getMostPopularProducts()
    {
        List<Product> products = getAllProducts();
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
