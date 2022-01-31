package com.localapp.controller;

import com.localapp.PayloadResponse.BusinessRegResponse;
import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.model.Business;
import com.localapp.model.Pincode;
import com.localapp.service.UserService;
import com.localapp.service.VendorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.localapp.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@RequestMapping("/vendor")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class VendorController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    VendorService vendorService;
    @Autowired
    UserService userService;

    //Post endpoint for vendors to register their business
    @PostMapping("/register/{userId}")
    public ResponseEntity<?> registerBusiness(@PathVariable("userId") int userId, @RequestBody Business newBusiness) {

        System.out.println(newBusiness);
        //check if user email already exist in database
        if (vendorService.businessExistsByGstin(newBusiness.getGstin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Business already exist by this gstin!"));
        }

        Set<Pincode> pinSet = new HashSet<>(); //Convert pincode list to set
        pinSet.addAll(newBusiness.getPincodes());
        System.out.println(pinSet);

        Business business = new Business(newBusiness.getBusinessName(), newBusiness.getBusinessCategory(), newBusiness.getAddress(), newBusiness.getGstin(), pinSet);
        System.out.println(business);
        Business businessRegistered = vendorService.saveVendorBusiness(business,userId);             //save new user details in database

        return ResponseEntity.ok(new BusinessRegResponse("Business registered successfully!", businessRegistered));

    }

    //Post endpoint to upload business license in database
    @PostMapping(value = "/uploadBusinessLicense/{bid}", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBusinessLicense(@PathVariable(value = "bid") int bid, @RequestParam(value = "file") MultipartFile image){

        if (!vendorService.existsById(bid)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Business does not exist!"));
        }
        System.out.println(bid+"  "+image);

        try {
            //add license of business in database
            byte[] imageBytes = image.getBytes();
            vendorService.uploadBusinessLicense(bid,imageBytes);
            return ResponseEntity.ok(new MessageResponse("License uploaded Successfully!"));
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Cannot upload license!"));
        }
    }

}
