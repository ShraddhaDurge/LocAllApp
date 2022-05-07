package com.localapp.Model;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "basket_items")
@AllArgsConstructor
@NoArgsConstructor
public class BasketItem {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "basket_id")
    int basketId;

    @Column(name = "quantity_selected")
    int quantSelected;

    @Column(name = "priceOfItem")
    double priceOfItem;

    @Column(name = "discount_price")
    double discountedPrice;

    @Column(name = "status")
    String status;

    @Column(name = "delivery_status")
    String deliveryStatus;

    @Column(name = "rating")
    int productRatingByUser;

    @Column(name = "orderTimestamp")
    String orderTimestamp;

    @Column(name = "deliveryTimestamp")
    String deliveryTimestamp;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_basket",
            joinColumns = @JoinColumn(name = "BasketItems_basket_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "basket_product",
            joinColumns = @JoinColumn(name = "BasketItems_basket_id"),
            inverseJoinColumns = @JoinColumn(name = "product_product_id")
    )
    private Product product;
}