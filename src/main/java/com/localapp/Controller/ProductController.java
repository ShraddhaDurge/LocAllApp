package com.localapp.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.localapp.Model.ProductTags;
import com.localapp.PayloadResponse.ProductCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.PayloadResponse.ProductRegResponse;
import com.localapp.Model.Product;
import com.localapp.Service.ProductService;
import com.localapp.Service.BusinessService;

@RequestMapping("/product")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    BusinessService businessService;

    @PostMapping("/add/{business_id}")
    public ResponseEntity<?> addProduct(@PathVariable("business_id") int business_id, @RequestBody Product newProduct) {

        System.out.println(newProduct);

        Product productRegistered = productService.saveVendorProduct(newProduct,business_id);             //save new user details in database

        return ResponseEntity.ok(new ProductRegResponse("Product registered successfully!", productRegistered));

    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProduct( @RequestBody Product updatedProduct) {

        System.out.println(updatedProduct);

        Product productUpdated = productService.updateVendorProduct(updatedProduct);         //save new user details in database

        return ResponseEntity.ok(new ProductRegResponse("Product Updated successfully!", productUpdated));

    }

    //Post endpoint to upload business license in database
    @PostMapping(value = "/uploadProductImage/{productId}", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(@PathVariable(value = "productId") int productId, @RequestParam(value = "file") MultipartFile image){

        if (!productService.existsById(productId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Product does not exist!"));
        }

        try {
            //add license of business in database
            byte[] imageBytes = image.getBytes();
            productService.uploadProductImage(productId,imageBytes);
            return ResponseEntity.ok(new MessageResponse("Product Image uploaded Successfully!"));
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Cannot upload Product Image!"));
        }
    }

    @RequestMapping(value = "/delete/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResponse> delete(@PathVariable(value = "productId") int productId) throws IOException {
        try {
            productService.deleteById(productId);
            return ResponseEntity.ok(new MessageResponse("Product Deleted Successfully!"));
        }
        catch (Exception e)
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Product could not be deleted!"));
        }
    }

    @RequestMapping(value = "/getList/{businessId}", method = RequestMethod.GET)
    public Set<Product> getBusinessProducts(@PathVariable(value = "businessId") int businessId) {
        return productService.getBusinessProducts(businessId);
    }

    //Get product data by productId
    @GetMapping(value = "/{id}")
    public Product getProductById(@PathVariable("id") int id) {
        return productService.getById(id);
    }

    @GetMapping(value = "/getProductTags")
    public List<ProductTags> getProductTags() {
        return productService.getAllTags();
    }

    @GetMapping(value = "/getProductCategories")
    public List<ProductCategoryResponse> getProductCategories() {
        return productService.getProductCategories();
    }

    @GetMapping(value = "/getMostPopularProducts")
    public List<Product> getMostPopularProducts() {
        return productService.getMostPopularProducts();
    }

    @GetMapping(value = "/getCategoryProducts/{category:[a-zA-Z &+-]*}")
    public List<Product> getCategoryProducts(@PathVariable("category")  String category) {
        return productService.getCategoryWiseProducts(category);
    }

}