package com.localapp.Service;

import com.localapp.Model.BasketItem;
import com.localapp.Model.CustomerProfile;
import com.localapp.Model.User;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class SendEmailService {

    @Autowired
    JavaMailSender javaMailSender;

    void sendHtmlMessage(String emailReceiver, String emailSubject, String htmlBody) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("LocAll Team <locall.ecommerce@gmail.com>");
        helper.setTo(emailReceiver);
        helper.setSubject(emailSubject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
    }

    public String parseThymeleafTemplate(String invoiceDate, String username, CustomerProfile customerProfile, List<BasketItem> basketItem, double totalPrice, String template) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("invoiceDate", invoiceDate);
        context.setVariable("userName", username);
        context.setVariable("billingAddress", customerProfile.getBillingAddress());
        context.setVariable("shippingAddress", customerProfile.getShippingAddress());
        context.setVariable("billingPincode", customerProfile.getBillingPincode());
        context.setVariable("shippingPincode", customerProfile.getShippingPincode());
        context.setVariable("basketItem", basketItem);
        context.setVariable("totalPrice", totalPrice);

        System.out.println(username + "  " + invoiceDate + "  " + totalPrice);

        return templateEngine.process(template, context);
    }
    public void generatePdfFromHtml(String html, int orderId) throws IOException, DocumentException {
        try {
            String fileName = orderId + ".pdf";
            System.out.println(fileName);
            String outputFolder = System.getProperty("user.home") + File.separator + fileName;
            OutputStream outputStream = new FileOutputStream(outputFolder);
            ITextRenderer renderer = new ITextRenderer();
            System.out.println("setDocumentFromString");
            renderer.setDocumentFromString(html);
            System.out.println("layout");
            renderer.layout();
            renderer.createPDF(outputStream);
            System.out.println("PDF GENERATED");
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendEmailWithAttachment(User user, String emailSubject, String emailText, String pathToAttachment) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setFrom("LocAll Team <locall.ecommerce@gmail.com>");
            helper.setSubject(emailSubject);
            helper.setText(emailText, true);
            //FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            File file = new File(pathToAttachment);
            helper.addAttachment("Invoice", file);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            System.out.println("Invoice not sent.");
            e.printStackTrace();
        }
    }
}