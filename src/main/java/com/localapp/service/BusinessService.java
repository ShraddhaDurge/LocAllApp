package com.localapp.service;

import com.localapp.model.Business;
import com.localapp.model.Pincode;
import com.localapp.model.User;
import com.localapp.repository.BusinessRepository;
import com.localapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class BusinessService {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    UserService userService;

    private static final Logger logger = LogManager.getLogger(UserService.class);


    //save business corresponding to specific vendor
    public Business saveVendorBusiness(Business business, int userId) {
        business.setUser(userService.findById(userId));
        businessRepository.save(business);
        return findByGstin(business.getGstin());

    }

    //save business in database
    public void saveBusiness(Business business) {

        businessRepository.save(business);
    }

    public Boolean businessExistsByGstin(String gstin) {
        return businessRepository.existsByGstin(gstin);
    }

    public Boolean existsById(int id) {
        return businessRepository.existsById(id);
    }

    public Business getById(int id) {
        return businessRepository.getById(id);
    }

    public Business findByGstin(String gstin) {
        return businessRepository.findByGstin(gstin);
    }

    //save business license in database
    public void uploadBusinessLicense(int bid, byte[] imageBytes) throws IOException {
        Business business = getById(bid);
        System.out.println(business + "  "+imageBytes);
        String encodedImage = Base64Utils.encodeToString(imageBytes);
        business.setLicense(encodedImage);
        saveBusiness(business);
    }
    public Business getBusinessVendor(User user) {
        // TODO Auto-generated method stub
        return businessRepository.findByUser(user);

    }
}
