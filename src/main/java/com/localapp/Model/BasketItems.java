package com.localapp.Model;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@Table(name = "basket_items")
@AllArgsConstructor
@NoArgsConstructor
public class BasketItems {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "basket_id")
    int basketId;

    @Column(name = "product_name")
    String productName;

    @Column(name = "product_image")
    String productImage;

    @Column(name = "quantity_selected")
    int quantSelected;

    @Column(name = "price")
    double price;

    @Column(name = "discount_price")
    double discountedPrice;

    @Column(name = "product_id")
    int productId;

    @Column(name = "status")
    String status;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_basket",
            joinColumns = @JoinColumn(name = "BasketItems_basket_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User user;
}