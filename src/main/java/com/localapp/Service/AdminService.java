package com.localapp.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.Model.Business;
import com.localapp.Repository.BusinessRepository;

import javax.mail.MessagingException;

@Service
public class AdminService {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    SendEmailService sendEmailService;

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

        Business business = businessRepository.getById(businessid);
        business.setStatus("Verified");
        businessRepository.save(business);

        String emailText =
                "<p> Congratulation " + business.getUser().getUsername() + "!!<br/>" +
                        "Your business "+ business.getBusinessName() + " has been successfully verified." +"<br/>" +
                        "You can now get started by adding your products on the website<br/>"+
                        "We are looking forward to working with you! <br/><br/>" +
                        "Happy selling!<br/>" +
                        "LocAll Team </p>";

        //Send Email Service
        try {
            sendEmailService.sendHtmlMessage(business,"Welcome! Business verified successfully", emailText);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public int rejectBusiness(int businessid) {
        // TODO Auto-generated method stub
        Business business = businessRepository.getById(businessid);

        String emailText =
                "<p> Dear " + business.getUser().getUsername() + ",<br/>" +
                        "We were unable to verify the information you provided when registering your business on LocAll.<br/>"+
                        "Unfortunately, this means that you will not be able to sell your products on LocAll.<br/>"+
                        "Please register again using valid credentials and appropriate documents.<br/>" +
                        "Thank you for your patience and understanding.<br/><br/>" +
                        "Kind Regards,<br/>" +
                        "LocAll Team </p>";

        //Send Email Service
        try {
            sendEmailService.sendHtmlMessage(business,"Business rejected", emailText);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        business.setPincodes(null);
        businessRepository.deleteById(businessid);
        return 0;
    }




}