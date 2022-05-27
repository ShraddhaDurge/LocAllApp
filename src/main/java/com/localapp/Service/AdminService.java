package com.localapp.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import com.localapp.Model.BasketItem;
import com.localapp.Model.Product;
import com.localapp.Model.User;
import com.localapp.PayloadResponse.*;
import com.localapp.Repository.BasketItemsRepository;
import com.localapp.Repository.ProductRepository;
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

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    BusinessService businessService;

    @Autowired
    BasketItemsRepository basketItemsRepository;

    @Autowired
    ProductRepository productRepository;

    public List<Business> findBusinesses() {
        List<Business> businesses = businessRepository.findByStatus("Pending");
        List<Business> pendingBusinesses =new ArrayList<>();

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
            sendEmailService.sendHtmlMessage(business.getUser().getEmail(),"Welcome! Business verified successfully", emailText);
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
            sendEmailService.sendHtmlMessage(business.getUser().getEmail(),"Business rejected", emailText);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        business.setPincodes(null);
        userService.deleteById(business.getUser().getId());
        businessRepository.deleteById(businessid);
        return 0;
    }


    public AdminAnalyticsResponse getAdminReportAnalytics() {
        List<User> users = userService.findAllUsers();
        List<Product> products = productService.getAllProducts();
        List<Business> businesses = businessService.findAllBusinesses();

        int totalCustomers = 0;
        int totalVendors = 0;
        int totalProducts = 0;
        int totalCategories;
        int totalSales = 0;
        double revenue = 0;

        Map<String, Integer> categories = new LinkedHashMap<>();
        Map<String, Integer> categorySales = new LinkedHashMap<>();
        //total customer
        for(User u : users){
            if(u.getRole().equalsIgnoreCase("customer"))
                totalCustomers++;
            if(u.getRole().equalsIgnoreCase("vendor"))
                totalVendors++;
        }

        //total products
        for(Product p : products){
            totalProducts++;
        }

        //total categories
        for(Business b : businesses) {
            String category= b.getBusinessCategory();
            category = category.replaceAll("Store", "");
            category = category.replaceAll("Shop", "");
            category = category.trim();

            if(categories.containsKey(category))
                categories.put(category, categories.get(category) + 1);
            else
                categories.put(category, 1);

            //total sales
            Set<Product> productSet = b.getProducts();

            for(Product p : productSet){
                totalSales += p.getTotalSales();

                //CategoryWiseSale
                if(categorySales.containsKey(category))
                    categorySales.put(category, categorySales.get(category)  + p.getTotalSales());
                else
                    categorySales.put(category, p.getTotalSales());
            }


        }

        totalCategories = categories.size();

        //total revenue generated
        List<BasketItem> basketItems = basketItemsRepository.findAll();

        for(BasketItem b : basketItems){
            if(b.getDeliveryStatus().equalsIgnoreCase("Delivered")){
                revenue += b.getDiscountedPrice();
            }
        }

        List<AdminCategorySales> adminCategorySales = new ArrayList<>();
        //category wise sales
        for (Map.Entry<String, Integer> sale : categorySales.entrySet()) {
            AdminCategorySales sales = new AdminCategorySales(sale.getKey(), sale.getValue());
            adminCategorySales.add(sales);
        }
        AdminAnalyticsResponse ar = new AdminAnalyticsResponse(totalSales, revenue, totalCustomers,
                totalVendors, totalProducts, totalCategories,adminCategorySales, getMonthWiseRevenue(),
                getTopProducts(), getLeastSellingProducts());
        return ar;
    }

    public List<MonthWiseRevenue> getMonthWiseRevenue(){
        List<BasketItem> basketItems = basketItemsRepository.findAll();

        List<MonthWiseRevenue> monthWiseRevenue = new ArrayList<>();
        Map<String, Double > monthRevenueChart = new LinkedHashMap<>();


        for(int i = 1; i <= 12; i++){
            String month = String.valueOf(Month.of(i)).substring(0, 3).toLowerCase();
            monthRevenueChart.put(month,0.0);
        }

        for(BasketItem basketItem : basketItems){
            if(basketItem.getDeliveryStatus().equals("Delivered"))
            {
                double productRev = basketItem.getDiscountedPrice();
                //month revenue
                String dateTime = basketItem.getDeliveryTimestamp();
                String months = dateTime.substring(3, 5);
                String month = String.valueOf(Month.of(Integer.parseInt(months)));
                String mon = month.substring(0, 3).toLowerCase();
                System.out.println(mon);
                if(monthRevenueChart.containsKey(mon))
                    monthRevenueChart.put(mon, monthRevenueChart.get(mon) + productRev);
                else
                    monthRevenueChart.put(mon, productRev);
            }
        }

        for (Map.Entry<String, Double> month : monthRevenueChart.entrySet()) {
            MonthWiseRevenue monthRevenue = new MonthWiseRevenue(month.getKey(), month.getValue());
            monthWiseRevenue.add(monthRevenue);
        }

        return monthWiseRevenue;
    }

    public List<VendorTopProducts> getLeastSellingProducts(){
        //Top Products
        List<VendorTopProducts> topProducts = new ArrayList<>();
        List<Product> products = productService.getAllProducts();
        List<BasketItem> basketItems = basketItemsRepository.findAll();
        List<Product> popularProducts = products.stream()
                .sorted(Comparator.comparing(Product::getTotalSales))
                .collect(Collectors.toList());

        int length = popularProducts.size();
        if(popularProducts.size() > 5)
            length = 5;

        for (int i = 0; i < length; i++) {
            //Top products revenue
            Product product = productRepository.findByProductName(popularProducts.get(i).getProductName());
            VendorTopProducts topProduct = new VendorTopProducts(product.getProductName(),product.getTotalSales() * product.getPrice(), product.getTotalSales(), product.getQuantAvailable() );
            topProducts.add(topProduct);
        }
        return  topProducts;
    }

    public List<VendorTopProducts> getTopProducts(){
        //Least Selling Products
        List<VendorTopProducts> topProducts = new ArrayList<>();
        List<BasketItem> basketItems = basketItemsRepository.findAll();
        Map<String, Double> productRevenue = new HashMap<>();

        //Product wise revenue
        for(BasketItem basketItem : basketItems){
            if(basketItem.getDeliveryStatus().equalsIgnoreCase("Delivered"))
            {
                double productRev = basketItem.getDiscountedPrice();
                System.out.println(productRev);

                if(productRevenue.containsKey(basketItem.getProduct().getProductName())) {
                    double pr = productRevenue.get(basketItem.getProduct().getProductName());
                    productRevenue.put(basketItem.getProduct().getProductName(), pr + productRev);
                    System.out.println(productRevenue);
                }
                else {
                    productRevenue.put(basketItem.getProduct().getProductName(), productRev);
                    System.out.println(productRevenue);
                }
            }
        }

        List<Map.Entry<String, Double> > list
                = new LinkedList<>(productRevenue.entrySet());
        Collections.sort(list,(i1, i2) -> i2.getValue().compareTo(i1.getValue()));

        int length = list.size();
        if(list.size() > 5)
            length = 5;

        for (int i = 0; i < length; i++) {
            //Top products revenue
            Product product = productRepository.findByProductName(list.get(i).getKey());
            VendorTopProducts topProduct = new VendorTopProducts(product.getProductName(),list.get(i).getValue(), product.getTotalSales(), product.getQuantAvailable() );
            topProducts.add(topProduct);
        }
        return  topProducts;
    }
}