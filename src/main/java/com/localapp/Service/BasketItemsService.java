package com.localapp.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.localapp.Model.Business;
import com.localapp.Model.User;
import com.localapp.Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.PayloadRequest.OrderRequest;
import com.localapp.Repository.BasketItemsRepository;
import com.localapp.Model.BasketItems;
import com.localapp.Model.Product;

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

    private static final Logger logger = LogManager.getLogger(BasketItemsService.class);

    public int numberOfItemsFromVendor(int businessId, List<BasketItems> basketItems) {
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
        // 1: Get All Product Id from BasketItems where status = unpaid & cust_id = custId
        try
        {
            List<BasketItems> basketItems = getFilteredBasketItems(custId);
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

    public BasketItems saveBasketItem(BasketItems basketItem) {
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

    public List<BasketItems> getFilteredBasketItems(int custId) {
        try {
            List<BasketItems> basketItems= basketItemsRepository.findByUser(userRepository.findById(custId));

            List<BasketItems> filteredBasketItems = new ArrayList<>();
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

            List<BasketItems> tempBasket = basketItemsRepository.findByUser(userRepository.findById(custId));

            System.out.println(tempBasket);
            if(tempBasket.size()!=0) {
                for (int i = 0; i < tempBasket.size(); i++) {
                    if (tempBasket.get(i).getProduct().getProductId() == order.getProductId() && tempBasket.get(i).getStatus().equalsIgnoreCase("unpaid")) {
                        int prevQuantity = tempBasket.get(i).getQuantSelected();
                        increaseTotalSalesOnInsertion(order);
                        tempBasket.get(i).setQuantSelected(prevQuantity + order.getQuantSelected());
                        tempBasket.get(i).setPriceOfItem(product.getPrice() * (prevQuantity + order.getQuantSelected()));
                        tempBasket.get(i).setDiscountedPrice(product.getPrice() * (prevQuantity + order.getQuantSelected()));
                        BasketItems savedBasketItem = saveBasketItem(tempBasket.get(i));
                        if (savedBasketItem.getProduct().getProductName() != null) {
                            logger.info("New Basket Item for Customer with CustId: {} and Product with ProductId: {} saved!", custId, order.getProductId());
                            return true;
                        }
                        return false;
                    }
                }
            }

            increaseTotalSalesOnInsertion(order);
            BasketItems basketItem = new BasketItems();
            basketItem.setUser(userRepository.findById(custId));
            basketItem.setPriceOfItem(product.getPrice() * order.getQuantSelected());
            basketItem.setDiscountedPrice(product.getPrice() * order.getQuantSelected());
            basketItem.setProduct(product);
            basketItem.setQuantSelected(order.getQuantSelected());
            basketItem.setStatus("unpaid");

            BasketItems savedBasketItem = saveBasketItem(basketItem);

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

    public BasketItems deleteBasketItem(int basketId) {
        try {
            BasketItems basketItem = getBasketById(basketId);
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

    public BasketItems getBasketById(int basketId) {
        try {
            logger.info("Deleting Basket Item with Basket Id: {}", basketId);
            return basketItemsRepository.getById(basketId);
        }
        catch(Exception e)
        {
            logger.error("Basket Item with BasketId: {} could not be deleted!",basketId);
            return null;
        }
    }
}