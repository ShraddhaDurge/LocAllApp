package com.localapp.Service;

import com.localapp.Model.*;
import com.localapp.PayloadRequest.BusinessRequest;
import com.localapp.PayloadRequest.UpdateBusinessRequest;
import com.localapp.PayloadResponse.*;
import com.localapp.Repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.Month;
import java.util.stream.Collectors;

@Service
public class BusinessService {

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    PincodeRepository pincodeRepository;

    @Autowired
    BasketItemsRepository basketItemsRepository;

    @Autowired
    CustomerProfileRepository customerProfileRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductRepository productRepository;

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

    public List<OrderManagementResponse> getCustomerOrders(int business_id) {

        List<BasketItem> basketItems = basketItemsRepository.findAll();
        Business business = businessRepository.findById(business_id);

        List<OrderManagementResponse> finalProducts = new ArrayList<>();
        List<BasketItem> basketItemsList = basketItems.stream()
                .sorted(Comparator.comparing(BasketItem::getBasketId).reversed())
                .collect(Collectors.toList());

        for(BasketItem basketItem : basketItemsList){
            if(!basketItem.getDeliveryStatus().equalsIgnoreCase("Undelivered") &&
                    business.getProducts().contains(basketItem.getProduct()))
            {
                CustomerProfile customer = customerProfileRepository.findByUser(basketItem.getUser());
                OrderManagementResponse order = new OrderManagementResponse(basketItem.getBasketId(),basketItem.getProduct().getProductImage(),basketItem.getProduct().getProductName(),
                        basketItem.getQuantSelected(),basketItem.getDiscountedPrice(),basketItem.getOrderTimestamp(), customer.getShippingAddress() +" "+ customer.getShippingPincode(),
                        basketItem.getDeliveryStatus());
                finalProducts.add(order);
            }
        }
        return finalProducts;
    }

    public String setOrderDeliveredStatus(int basketId) {
        BasketItem basketItem = basketItemsRepository.getById(basketId);
        basketItem.setDeliveryStatus("Delivered");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String dateTime = dateFormat.format(timestamp);

        basketItem.setDeliveryTimestamp(dateTime);

        basketItemsRepository.save(basketItem);
        return basketItem.getDeliveryStatus();
    }

    public VendorAnalyticsResponse getVendorReportAnalytics(int businessId) {
        Business business = getById(businessId);
        List<BasketItem> basketItems = basketItemsRepository.findAll();
        List<Product> products= new ArrayList<>(business.getProducts());
        List<ProductWiseSales> productWiseSales = new ArrayList<>();
        Map<String, Double> productRevenue = new HashMap<>();

        Set<Integer> buyers = new HashSet<>();
        int totalSales = 0;
        int totalStocks = 0;
        double revenue = 0;

        for(Product product : products) {
            totalSales += product.getTotalSales();
            totalStocks++;
        }

        //Product wise revenue
        for(BasketItem basketItem : basketItems){
            if(basketItem.getDeliveryStatus().equalsIgnoreCase("Delivered") &&
                    business.getProducts().contains(basketItem.getProduct()))
            {
                double productRev = basketItem.getDiscountedPrice();
                System.out.println(productRev);
                revenue += productRev;
                buyers.add(basketItem.getUser().getId());

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
        for(Map.Entry<String, Double> entry : productRevenue.entrySet()) {
            Product product = productRepository.findByProductName(entry.getKey());
            ProductWiseSales productSales = new ProductWiseSales(entry.getKey() , product.getTotalSales(), entry.getValue());
            productWiseSales.add(productSales);
        }


        VendorAnalyticsResponse vr = new VendorAnalyticsResponse(totalSales,revenue,totalStocks,buyers.size(), productWiseSales, getMonthWiseRevenue(business), getVendorTopProducts(productWiseSales), getVendorLastProducts(productWiseSales));
        System.out.println(vr);

        return vr;
    }

    public List<VendorTopProducts> getVendorTopProducts(List<ProductWiseSales> productWiseSales){
        //Top Products
        List<VendorTopProducts> vendorTopProducts = new ArrayList<>();

        int length = productWiseSales.size();
        if(productWiseSales.size() > 5)
            length = 5;

        List<ProductWiseSales> popularProducts = productWiseSales.stream()
                .sorted(Comparator.comparing(ProductWiseSales::getSales).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < length; i++) {
            //Top products revenue
            Product product = productRepository.findByProductName(popularProducts.get(i).getProduct());
            VendorTopProducts topProducts = new VendorTopProducts(popularProducts.get(i).getProduct(),popularProducts.get(i).getRevenue(), popularProducts.get(i).getSales(), product.getQuantAvailable() );
            vendorTopProducts.add(topProducts);
        }
        return  vendorTopProducts;
    }

    public List<VendorTopProducts> getVendorLastProducts(List<ProductWiseSales> productWiseSales){
        //Top Products
        List<VendorTopProducts> vendorTopProducts = new ArrayList<>();

        int length = productWiseSales.size();
        if(productWiseSales.size() > 5)
            length = 5;

        List<ProductWiseSales> lastProducts = productWiseSales.stream()
                .sorted(Comparator.comparing(ProductWiseSales::getSales))
                .collect(Collectors.toList());

        for (int i = 0; i < length; i++) {
            //Top products revenue
            if(lastProducts.get(i).getSales() < 10) {
                Product product = productRepository.findByProductName(lastProducts.get(i).getProduct());
                VendorTopProducts topProducts = new VendorTopProducts(lastProducts.get(i).getProduct(), lastProducts.get(i).getRevenue(), lastProducts.get(i).getSales(), product.getQuantAvailable());
                vendorTopProducts.add(topProducts);
            }
        }
        return  vendorTopProducts;
    }

    public List<MonthWiseRevenue> getMonthWiseRevenue(Business business){

        List<BasketItem> basketItems = basketItemsRepository.findAll();
        List<Product> products= new ArrayList<>(business.getProducts());

        List<MonthWiseRevenue> monthWiseRevenue = new ArrayList<>();
        Map<String, Double > monthRevenueChart = new LinkedHashMap<>();


        for(int i = 1; i <= 12; i++){
            String month = String.valueOf(Month.of(i)).substring(0, 3).toLowerCase();
            monthRevenueChart.put(month,0.0);
        }

        for(BasketItem basketItem : basketItems){
            if(basketItem.getDeliveryStatus().equalsIgnoreCase("Delivered") &&
                    business.getProducts().contains(basketItem.getProduct()))
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


}
