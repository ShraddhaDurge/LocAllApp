package com.localapp.Controller;

import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.Model.CustomerProfile;
import com.localapp.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/customer")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    //Post endpoint for vendors to register their business
    @PostMapping("/updateCustomerProfile/{userId}")
    public ResponseEntity<?> updateCustomerProfile(@PathVariable("userId") int userId, @RequestBody CustomerProfile customerProfile) {

        System.out.println(customerProfile);

        CustomerProfile c = customerService.saveCustomerProfile(userId,customerProfile);            //save customer profile details in database

        return ResponseEntity.ok(new MessageResponse("Customer Profile updated successfully!"));

    }

    @GetMapping("/getCustomerProfile/{userId}")
    public CustomerProfile getCustomerProfile(@PathVariable("userId") int userId) {
        return customerService.getCustomerProfile(userId);
    }



}
