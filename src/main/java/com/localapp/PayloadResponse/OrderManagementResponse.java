package com.localapp.PayloadResponse;

import java.util.List;

import com.localapp.Model.BasketItem;
import com.localapp.Model.CustomerProfile;
import com.localapp.Model.Product;
import com.localapp.Model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderManagementResponse {
//    List<BasketItem> finalProducts;
//    List<CustomerProfile> finalUsers;
    int id;
    String productImage;
    String productName;
    int quantSelected;
    double discountedPrice;
    String orderDate;
    String shippingAddress;
    String deliveryStatus;

}