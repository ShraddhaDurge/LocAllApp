package com.localapp.Service;

import com.localapp.Model.CustomerProfile;
import com.localapp.Model.User;
import com.localapp.Repository.CustomerProfileRepository;
import com.localapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerProfileRepository profileRepository;

    @Autowired
    UserRepository userRepository;

    public CustomerProfile saveCustomerProfile(int userId, CustomerProfile customerProfile) {

        User user = userRepository.findById(userId);
        CustomerProfile cp = findCustomerProfileByUser(user);

        if(cp == null) {
            customerProfile.setUser(user);
            profileRepository.save(customerProfile);
            return customerProfile;
        }
        else {
            cp.setBillingAddress(customerProfile.getBillingAddress());
            cp.setShippingAddress(customerProfile.getShippingAddress());
            cp.setShippingPincode(customerProfile.getShippingPincode());
            cp.setBillingPincode(customerProfile.getBillingPincode());
            profileRepository.save(cp);
            return cp;
        }


    }

    public CustomerProfile getCustomerProfile(int userId) {
        List<CustomerProfile> allProfiles = profileRepository.findAll();

        CustomerProfile profile = null;
        try {
            if(allProfiles == null) {
                profile = null;
            }
            else {
                for (CustomerProfile p : allProfiles) {
                    if (p.getUser().getId() == userId) {
                        profile = p;
                        break;
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        return profile;
    }
    public CustomerProfile findCustomerProfileByUser(User user) {
        return profileRepository.findByUser(user);
    }


}
