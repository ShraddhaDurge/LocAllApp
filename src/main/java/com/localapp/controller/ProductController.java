package com.localapp.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.PayloadResponse.ProductRegResponse;
import com.localapp.model.Product;
import com.localapp.model.ProductCategoryTags;
import com.localapp.service.ProductService;
import com.localapp.service.BusinessService;

@RequestMapping("/product")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    BusinessService businessService;

    @PostMapping("/register/{business_id}")
    public ResponseEntity<?> registerProduct(@PathVariable("business_id") int business_id, @RequestBody Product newProduct) {

        System.out.println(newProduct);

        Set<ProductCategoryTags> productTags = new HashSet<>(); //Convert Product Tags list to set
        productTags.addAll(newProduct.getProductTags());
        System.out.println(productTags);

        Product product = new Product(newProduct.getProductName(), newProduct.getQuantAvailable(), newProduct.getPrice(), newProduct.getProductDesc(),newProduct.getProductTags());
        System.out.println(product);
        Product productRegistered = productService.saveVendorProduct(product,business_id);             //save new user details in database

        return ResponseEntity.ok(new ProductRegResponse("Product registered successfully!", productRegistered));

    }

    @PostMapping("/update/{business_id}")
    public ResponseEntity<?> updateProduct(@PathVariable("business_id") int business_id, @RequestBody Product updatedProduct) {

        System.out.println(updatedProduct);

        Set<ProductCategoryTags> productTags = new HashSet<>(); //Convert Product Tags list to set
        productTags.addAll(updatedProduct.getProductTags());
        System.out.println(productTags);

        System.out.println(updatedProduct);
        Product productUpdated = productService.updateVendorProduct(updatedProduct,business_id);         //save new user details in database

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
        System.out.println(productId+"  "+image);

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
    @ResponseBody
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

}