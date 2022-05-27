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

    @Autowired
    CustomerService customerService;

    @Autowired
    UserService userService;

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
        if(!basketItems.isEmpty()) {
            for (BasketItem basketItem : basketItems) {
                basketItemsRepository.delete(basketItem);
            }
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

    public List<ProductCategoryResponse> getProductCategories(int pincode) {
        List<Business> businesses = businessService.findAllBusinesses();
        List<ProductCategoryResponse> productCategories = new ArrayList<>();

        for(Business b : businesses) {
            Set<Pincode> bPincodes = b.getPincodes();
            if(pincode != 0) {
                for (Pincode pin : bPincodes) {
                    if (pin.getPincode() == pincode) {
                        addCategory(b, productCategories);
                    }
                }
            } else {
                addCategory(b, productCategories);
            }
        }
        return productCategories;
    }

    public String trimCategory(String category) {
        category = category.replaceAll("Store", "");
        category = category.replaceAll("Shop", "");
        return category.trim();
    }

    public List<ProductCategoryResponse> addCategory(Business b,List<ProductCategoryResponse> productCategories) {
        String category = trimCategory(b.getBusinessCategory());

        if (!b.getProducts().isEmpty()) {
            Optional<Product> p = b.getProducts().stream().findFirst();
            ProductCategoryResponse pc = new ProductCategoryResponse(category, p.get().getProductImage());
            productCategories.add(pc);
        }

        return productCategories;
    }

    public List<Product> getCategoryWiseProducts(String getcategory, int pincode) {
        List<Business> businesses = businessService.findAllBusinesses();
        List<Product> categoryProducts = new ArrayList<>();

        for(Business b : businesses) {
            Set<Pincode> bPincodes = b.getPincodes();
            if(pincode != 0) {
                for (Pincode pin : bPincodes) {
                    if (pin.getPincode() == pincode) {
                        getCategoryProduct(b,getcategory, categoryProducts);
                    }
                }
            } else {
                getCategoryProduct(b,getcategory,categoryProducts);
            }
        }
        return categoryProducts;
    }

    public List<Product> getCategoryProduct(Business b, String getcategory, List<Product> categoryProducts) {
        String category = trimCategory(b.getBusinessCategory());
        if (category.equalsIgnoreCase(getcategory)) {
            if (!b.getProducts().isEmpty()) {
                for (Product p : b.getProducts())
                    categoryProducts.add(p);
            }
        }
        return categoryProducts;
    }

    public List<Product> getMostPopularProducts(int pincode)
    {
        List<Product> products = getAllProducts();
        List<Product> popularProducts = products.stream()
                .sorted(Comparator.comparing(Product::getTotalSales).reversed())
                .collect(Collectors.toList());

        List<Product> mostPopularProducts=new ArrayList<>();

        if(popularProducts!=null) {
            for (Product product : popularProducts) {
                Business b = businessService.getProductBusiness(product);

                if(b != null && mostPopularProducts.size() <= 10) {
                    Set<Pincode> bPincodes = b.getPincodes();
                    if(pincode != 0) {
                        for (Pincode p : bPincodes) {
                            if (p.getPincode() == pincode) {
                                mostPopularProducts.add(product);
                            }
                        }
                    } else {
                        mostPopularProducts.add(product);
                    }

                }
            }
        }
        return mostPopularProducts;
    }

    public List<Product> getTagWiseProducts(String tag) {
        List<Product> products = productRepository.findAll();
        List<Product> tagWiseProducts = new ArrayList<>();
        for(Product p : products) {
            Set<ProductTags> productTags= p.getProductTags();
            if(!p.getProductTags().isEmpty()) {
                for (ProductTags pt : productTags) {
                    if (pt.getTag().equalsIgnoreCase(tag)) {
                        tagWiseProducts.add(p);
                    }
                }
            }
        }
        return tagWiseProducts;
    }
}
