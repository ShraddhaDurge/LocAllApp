package com.localapp.Controller;

import com.localapp.PayloadRequest.BusinessRequest;
import com.localapp.PayloadRequest.UpdateBusinessRequest;
import com.localapp.PayloadResponse.BusinessRegResponse;
import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.Model.Business;
import com.localapp.PayloadResponse.OrderManagementResponse;
import com.localapp.PayloadResponse.VendorAnalyticsResponse;
import com.localapp.Service.BusinessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RequestMapping("/vendor")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class BusinessController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    BusinessService vendorService;

    //Post endpoint for vendors to register their business
    @PostMapping("/register/{userId}")
    public ResponseEntity<?> registerBusiness(@PathVariable("userId") int userId, @RequestBody BusinessRequest newBusiness) {

        System.out.println(newBusiness);
        //check if user email already exist in database
        if (vendorService.businessExistsByGstin(newBusiness.getGstin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Business already exist by this gstin!"));
        }

        Business businessRegistered = vendorService.saveVendorBusiness(newBusiness,userId);             //save new user details in database

        return ResponseEntity.ok(new BusinessRegResponse("Business registered successfully!", businessRegistered,businessRegistered.getUser().getRole()));

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

    //Post endpoint for vendors to register their business
    @PostMapping("/updateBusiness/{userId}")
    public ResponseEntity<?> updateBusiness(@PathVariable("userId") int userId, @RequestBody UpdateBusinessRequest business) {

        System.out.println(business);

        Business businessRegistered = vendorService.updateBusiness(business,userId);             //save new user details in database

        return ResponseEntity.ok(new BusinessRegResponse("Business updated successfully!", businessRegistered,businessRegistered.getUser().getRole()));

    }

    //Post endpoint for vendors to register their business
    @GetMapping("/getBusiness/{userId}")
    public Business getBusiness(@PathVariable("userId") int userId) {
        return vendorService.getBusiness(userId);
    }

    @GetMapping("/getCustomerOrders/{business_id}")
    public List<OrderManagementResponse> getOrders(@PathVariable("business_id") int business_id) {
//        System.out.println(vendorService.getCustomerOrders(business_id));
        return vendorService.getCustomerOrders(business_id);
    }

    @GetMapping(value = "/setOrderDeliveredStatus/{basketId}")
    public String setOrderDeliveredStatus(@PathVariable (value = "basketId") int basketId) {
        return vendorService.setOrderDeliveredStatus(basketId);
    }

    @GetMapping(value = "/getReportAnalytics/{businessId}")
    public VendorAnalyticsResponse getReportAnalytics(@PathVariable (value = "businessId") int businessId) {
        return vendorService.getVendorReportAnalytics(businessId);
    }

}
