package com.localapp.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.localapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.model.Business;
import com.localapp.repository.BusinessRepository;

@Service
public class AdminService {

    @Autowired
    BusinessRepository businessRepository;

    public List<Business> findBusinesses() {
        List<Business> businesses = businessRepository.findByStatus("Pending");
        List<Business> pendingBusinesses =new ArrayList<>();
//        while(businesses.size()>10) {
//            businesses.remove(10);
//        }
        Iterator<Business> iterator = businesses.iterator();
        for (int i = 0; i < 10 && iterator.hasNext(); i++)
            pendingBusinesses.add(iterator.next());

        return pendingBusinesses;
    }

    public int acceptBusiness(int businessid) {

        Business bus = businessRepository.getById(businessid);
        bus.setStatus("Verified");
        businessRepository.save(bus);

        return 0;
    }

    public int rejectBusiness(int businessid) {
        // TODO Auto-generated method stub
        businessRepository.deleteById(businessid);
        return 0;
    }




}