package com.localapp.Controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localapp.Model.Business;
import com.localapp.Service.AdminService;

@RequestMapping("/admin")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class AdminController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "/vendorsToVerify")
    public List<Business> vendorsToVerify() {
        List<Business> businesses = adminService.findBusinesses();
        if(businesses.size()==0)
        {
            System.out.println("No Vendors to Verify");
            return null;
        }
        System.out.println(businesses.get(0).getBusinessName());
        return businesses;
    }

    @RequestMapping(value = "/acceptVendor/{id}")
    public boolean acceptVendor(@PathVariable("id") int businessId) {

        int res = adminService.acceptBusiness(businessId);
        return true;
    }
    @RequestMapping(value = "/rejectVendor/{id}")
    public boolean denyVendor(@PathVariable("id") int businessId) {

        int res = adminService.rejectBusiness(businessId);
        return true;
    }

}