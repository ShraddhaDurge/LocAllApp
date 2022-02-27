package com.localapp.Service;

import com.localapp.Model.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class SendEmailService {

    @Autowired
    JavaMailSender javaMailSender;

    void sendHtmlMessage(Business business, String emailSubject, String htmlBody) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("LocAll Team <locall.ecommerce@gmail.com>");
        helper.setTo(business.getUser().getEmail());
        helper.setSubject(emailSubject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
    }
}