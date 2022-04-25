package com.localapp.Service;

import com.localapp.Model.Product;
import com.localapp.PayloadRequest.BusinessRequest;
import com.localapp.PayloadRequest.UpdateBusinessRequest;
import com.localapp.Model.Business;
import com.localapp.Model.Pincode;
import com.localapp.Model.User;
import com.localapp.Repository.BusinessRepository;
import com.localapp.Repository.PincodeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BusinessService {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    PincodeRepository pincodeRepository;

    @Autowired
    UserService userService;

    private static final Logger logger = LogManager.getLogger(UserService.class);


    //save business corresponding to specific vendor
    public Business saveVendorBusiness(BusinessRequest businessRequest, int userId) {
        Set<Pincode> pincodes = businessRequest.getPincodes();
        Set<Pincode> businessPincodes = new HashSet<>();
        for(Pincode pin: pincodes) {
            Pincode p = pincodeRepository.getById(pin.getPincode());
            businessPincodes.add(p);
        }
        Business business = new Business(businessRequest.getBusinessName(), businessRequest.getBusinessCategory(), businessRequest.getAddress(), businessRequest.getGstin(), businessPincodes, "Pending");
        business.setUser(userService.findById(userId));
        businessRepository.save(business);
        return businessRepository.findById(business.getBusiness_id());

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

    public List<Business> findAllBusinesses() {
        return businessRepository.findAll();
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
        return businessRepository.findByUser(user);

    }
    public Business getBusiness(int userId) {
        return businessRepository.findByUser(userService.findById(userId));
    }

    public Business updateBusiness(UpdateBusinessRequest businessRequest, int userId) {
        Business business = getBusiness(userId);
        business.setBusinessName(businessRequest.getBusinessName());
        business.setBusinessCategory(businessRequest.getBusinessCategory());
        business.setAddress(businessRequest.getAddress());
        Set<Pincode> pincodes = businessRequest.getPincodes();
        Set<Pincode> businessPincodes = new HashSet<>();
        for(Pincode pin: pincodes) {
            Pincode p = pincodeRepository.getById(pin.getPincode());
            businessPincodes.add(p);
        }
        System.out.println(businessPincodes);
        business.setPincodes(businessPincodes);
        saveBusiness(business);
        return business;

    }

    public Business getProductBusiness(Product product) {

        List<Business> vendors = businessRepository.findAll();
        for(Business b : vendors) {
            Set<Product> vendorProducts = b.getProducts();
            if(vendorProducts.contains(product))
                return b;
        }
        return null;
    }
}
