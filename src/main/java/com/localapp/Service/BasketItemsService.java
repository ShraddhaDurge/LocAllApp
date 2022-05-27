package com.localapp.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.localapp.Model.*;
import com.localapp.PayloadResponse.ProductWiseSales;
import com.localapp.Repository.CustomerProfileRepository;
import com.localapp.Repository.UserRepository;
import com.lowagie.text.DocumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.PayloadRequest.OrderRequest;
import com.localapp.Repository.BasketItemsRepository;

import javax.mail.MessagingException;

@Service
public class BasketItemsService {

    @Autowired
    BasketItemsRepository basketItemsRepository;

    @Autowired
    ProductService productService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessService businessService;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    CustomerProfileRepository customerProfileRepository;

    private static final Logger logger = LogManager.getLogger(BasketItemsService.class);

    public int numberOfItemsFromVendor(int businessId, List<BasketItem> basketItems) {
        try
        {
            int totalNumOfItems = 0;
            for(int i=0; i<basketItems.size();i++)
            {
                int productId = basketItems.get(i).getProduct().getProductId();
                Product product = productService.getById(productId);
                if(product!=null)
                {
                    Business vendor = businessService.getById(businessId);
                    Set<Product> vendorProducts = vendor.getProducts();

                    if(vendorProducts.contains(product))
                    {
                        totalNumOfItems += basketItems.get(i).getQuantSelected();
                    }
                }
            }
            logger.info("Number of Items from Vendor with ID {} are {}",businessId,totalNumOfItems);
            return totalNumOfItems;
        }
        catch(Exception e)
        {
            logger.error("Number of Items from Vendor with ID {} could not be calculated",businessId);
            return -1;
        }
    }


    public double calculateDiscountedPrice(int custId) {
        // 1: Get All Product Id from BasketItem where status = unpaid & cust_id = custId
        try
        {
            List<BasketItem> basketItems = getFilteredBasketItems(custId);
            //2: For every product compare quantity of product selected with min_products and calculate discount
            double totalDiscountedPrice = 0;
            if(basketItems!=null)
            {
                for(int i = 0; i<basketItems.size(); i++)
                {
                    int productId = basketItems.get(i).getProduct().getProductId();
                    Product product = productService.getById(productId);
                    if(product == null) {
                        logger.error("Personalised discount could not be calculated for Customer with ID: {} since Product was not found!",custId);
                        return -1;
                    }
                    Business vendor = businessService.getProductBusiness(product);
                    int minQuantity = product.getMinProducts();
                    int currentQuantity = numberOfItemsFromVendor(vendor.getBusiness_id(), basketItems);
                    double discount = 0;
                    if(currentQuantity >= minQuantity)
                    {
                        double maxDiscount = product.getMaxDiscount();
                        discount = maxDiscount * (currentQuantity - minQuantity + 1) / currentQuantity;
                        if(discount > maxDiscount)
                            discount = maxDiscount;
                    }
                    int quantity = basketItems.get(i).getQuantSelected();
                    double discountedPricePerProduct = product.getPrice() * (1-(discount/100)) * quantity;
                    discountedPricePerProduct = Math.round(discountedPricePerProduct * 100.0) / 100.0;
                    basketItems.get(i).setDiscountedPrice(discountedPricePerProduct);
                    saveBasketItem(basketItems.get(i));
                    totalDiscountedPrice += discountedPricePerProduct;
                }
            }
            logger.info("Personalised discount for Customer with ID: {} is {}!",custId,totalDiscountedPrice);

            return totalDiscountedPrice;
        }
        catch(Exception e)
        {

            logger.error("Personalised discount could not be calculated for Customer with ID: {}!",custId);
            return -1;
        }
    }

    public BasketItem saveBasketItem(BasketItem basketItem) {
        try {
            logger.info("Saving new Basket Item with Basket Id: {}", basketItem.getBasketId());
            return basketItemsRepository.save(basketItem);
        }
        catch(Exception e)
        {
            logger.error("New Basket Item could not be saved!");
            return null;
        }
    }

    public List<BasketItem> getFilteredBasketItems(int custId) {
        try {
            List<BasketItem> basketItems= basketItemsRepository.findByUser(userRepository.findById(custId));

            List<BasketItem> filteredBasketItems = new ArrayList<>();
            int i = 0;
            int j = basketItems.size();
            while(i<j)
            {
                if(basketItems.get(i).getStatus().equalsIgnoreCase("unpaid"))
                    filteredBasketItems.add(basketItems.get(i));
                i++;
            }
            logger.info("Basket Items obtained for Customer with Cust Id: {}",custId);
            return filteredBasketItems;
        }
        catch(Exception e)
        {
            logger.error("Basket Items obtained for Customer with Cust Id: {} could not be obtained",custId);
            return null;
        }
    }


    public boolean saveBasketItemToRepo(int custId, OrderRequest order) {
        try {
            //check for custId if product Id exists in basket Items and status = unpaid, then increase qty
            Product product = productService.getById(order.getProductId());
            if(product == null) {
                logger.error("New Basket Item for Customer with CustId: {} and Product with ProductId: {} could not be saved since Product was not found!", custId, order.getProductId());
                return false;
            }
            if(product.getQuantAvailable()<order.getQuantSelected())
            {
                logger.error("Required Stock Not Available");
                return false;
            }

            List<BasketItem> tempBasket = basketItemsRepository.findByUser(userRepository.findById(custId));

            if(tempBasket.size()!=0) {
                for (int i = 0; i < tempBasket.size(); i++) {
                    if (tempBasket.get(i).getProduct().getProductId() == order.getProductId() && tempBasket.get(i).getStatus().equalsIgnoreCase("unpaid")) {
                        int prevQuantity = tempBasket.get(i).getQuantSelected();
                        increaseTotalSalesOnInsertion(order);
                        tempBasket.get(i).setQuantSelected(prevQuantity + order.getQuantSelected());
                        tempBasket.get(i).setPriceOfItem(product.getPrice() * (prevQuantity + order.getQuantSelected()));
                        tempBasket.get(i).setDiscountedPrice(product.getPrice() * (prevQuantity + order.getQuantSelected()));
                        BasketItem savedBasketItem = saveBasketItem(tempBasket.get(i));
                        if (savedBasketItem.getProduct().getProductName() != null) {
                            logger.info("New Basket Item for Customer with CustId: {} and Product with ProductId: {} saved!", custId, order.getProductId());
                            return true;
                        }
                        return false;
                    }
                }
            }

            increaseTotalSalesOnInsertion(order);
            BasketItem basketItem = new BasketItem();
            basketItem.setUser(userRepository.findById(custId));
            basketItem.setPriceOfItem(product.getPrice() * order.getQuantSelected());
            basketItem.setDiscountedPrice(product.getPrice() * order.getQuantSelected());
            basketItem.setProduct(product);
            basketItem.setQuantSelected(order.getQuantSelected());
            basketItem.setStatus("Unpaid");
            basketItem.setDeliveryStatus("Undelivered");
            basketItem.setDeliveryTimestamp("-");
            basketItem.setProductRatingByUser(0);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            basketItem.setOrderTimestamp(sdf3.format(timestamp));
            System.out.println(timestamp);

            BasketItem savedBasketItem = saveBasketItem(basketItem);

            if(savedBasketItem.getProduct().getProductName()!=null)
            {
                logger.info("New Basket Item for Customer with CustId: {} and Product with ProductId: {} saved!", custId, order.getProductId());
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            logger.error("New Basket Item for Customer with CustId: {} and Product with ProductId: {} could not be saved!", custId, order.getProductId());
            logger.error(e);
            return false;
        }
    }

    public BasketItem deleteBasketItem(int basketId) {
        try {
            BasketItem basketItem = getBasketById(basketId);
            if(basketItem!=null)
            {
                int quantity = basketItem.getQuantSelected();
                int productId = basketItem.getProduct().getProductId();
                OrderRequest order = new OrderRequest();
                order.setProductId(productId);
                order.setQuantSelected(quantity);
                basketItem.setPriceOfItem(quantity * basketItem.getProduct().getPrice());
                reduceTotalSalesOnDeletion(order);
            }
            logger.info("Deleting Basket Item with Basket Id: {}", basketId);
            return basketItemsRepository.deleteById(basketId);
        }
        catch (Exception e) {
            logger.error("BasketItem with Id: {} could not be deleted!", basketId);
            return null;
        }
    }

    public void reduceTotalSalesOnDeletion(OrderRequest order) {
        try {
            Product product = productService.getById(order.getProductId());
            if(product != null) {
                int totalSales = product.getTotalSales();
                totalSales = totalSales - order.getQuantSelected();
                if(totalSales<0)
                    product.setTotalSales(0);
                product.setTotalSales(totalSales);
            }
            logger.info("Total Sales Reduced!");
        }
        catch (Exception e) {
            logger.error("Total Sales could not be Reduced!");
        }

    }

    public void increaseTotalSalesOnInsertion(OrderRequest order) {
        try {
            Product product = productService.getById(order.getProductId());
            if(product != null) {
                int totalSales = product.getTotalSales();
                totalSales += order.getQuantSelected();
                product.setTotalSales(totalSales);
            }
            logger.info("Total Sales Increased!");
        }
        catch (Exception e) {
            logger.error("Total Sales could not be Increased!");
        }

    }

    public BasketItem getBasketById(int basketId) {
        try {
            logger.info("get Basket Item with Basket Id: {}", basketId);
            return basketItemsRepository.getById(basketId);
        }
        catch(Exception e)
        {
            logger.error("Basket Item with BasketId: {} could not be deleted!",basketId);
            return null;
        }
    }

    public List<BasketItem> getPastOrders(int custId) {
        List<BasketItem> pastOrders = new ArrayList<>();
        List<BasketItem> basketItemList = basketItemsRepository.findAll();
        List<BasketItem> basketItems = basketItemList.stream()
                .sorted(Comparator.comparing(BasketItem::getBasketId).reversed())
                .collect(Collectors.toList());
        for(BasketItem basketItem : basketItems) {
            if(basketItem.getUser() != null && basketItem.getUser().getId() == custId && !basketItem.getDeliveryStatus().equalsIgnoreCase("Undelivered") ) {
                    pastOrders.add(basketItem);
            }
        }
        return pastOrders;
    }

    public int saveProductRating(int basketId, int rating) {
        BasketItem basketItem = basketItemsRepository.getById(basketId);
        basketItem.setProductRatingByUser(rating);

        Product product = basketItem.getProduct();
        List<BasketItem> basketItemList = basketItemsRepository.findAll();
        int userCount = 0;
        int ratingSum = 0;
        for(BasketItem basketItems : basketItemList) {
            if(basketItems.getProduct() == product && basketItems.getStatus().equalsIgnoreCase("paid")) {

                System.out.println(basketItems.getBasketId());

                ratingSum += basketItems.getProductRatingByUser();

                userCount++;
            }
        }

        double avgRating = ratingSum / userCount;
        product.setRating((int) Math.round(avgRating));
        saveBasketItem(basketItem);
        productService.saveProduct(product);
        return basketItem.getProductRatingByUser();
    }

    public boolean updatePaymentStatus(int customerId)
    {
        List<BasketItem> custOrder = getFilteredBasketItems(customerId);

        double cost = calculateDiscountedPrice(customerId);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String dateTime = dateFormat.format(timestamp);

        int orderId = custOrder.get(0).getBasketId();

        for(BasketItem basketItem : custOrder) {
            Product product = basketItem.getProduct();
            int productQuantity = product.getQuantAvailable();
            if (productQuantity > 0)
                productQuantity--;

            int totalSales = product.getTotalSales() + basketItem.getQuantSelected();
            product.setTotalSales(totalSales);
            System.out.println(totalSales);
            productService.saveProduct(product);
            basketItem.setStatus("Paid");
            basketItem.setDeliveryStatus("Ongoing");
            basketItem.setOrderTimestamp(dateTime);
            System.out.println(dateTime);
            basketItemsRepository.save(basketItem);

        }

        User user = userRepository.findById(customerId);
        System.out.println(user);
        CustomerProfile cp = customerProfileRepository.findByUser(user);
        String emailText =
                "<p> Dear " + user.getUsername() + ",<br/>" +
                        "Thank you for the purchase! Your payment for order is received successfully. <br/>"+
                        "Please find invoice of your order in the attachment.<br/>"+
                        "We will deliver your order at your doorstep as soon as possible.<br/><br/>"+
                        "Kind Regards,<br/>" +
                        "LocAll Team </p>";

        String invoiceDate = dateTime.substring(0, Math.min(dateTime.length(), 10));

        String html = sendEmailService.parseThymeleafTemplate(invoiceDate, user.getUsername(), cp, custOrder, cost, "invoice");
        try {
            System.out.println("PDF Generating..");
            sendEmailService.generatePdfFromHtml(html, orderId);
            String pathToAttachment = System.getProperty("user.home") + File.separator + orderId + ".pdf";
            System.out.println(pathToAttachment);
            System.out.println("Sending Invoice..");
            sendEmailService.sendEmailWithAttachment(user,"Your Payment is Successful!", emailText, pathToAttachment);
            System.out.println("Invoice sent");
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

        return true;
    }
}