package com.localapp.Model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "product_id")
    int productId ;

    @Column(name = "product_name")
    String productName;

    @Column(name = "quantity_available")
    int quantAvailable ;

    @Column(name = "price")
    double price;

    @Column(name = "product_image")
    String productImage;

    @Column(name = "product_desc")
    String productDesc;

    @Column(name = "min_no_products")
    int minProducts;

    @Column(name = "max_discount")
    double maxDiscount;

    @Column(name = "total_sales")
    int totalSales;

    @Column(name = "rating")
    int rating;

    @ManyToMany(fetch = FetchType.EAGER,  cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinTable(name = "product_tags_relation",
            joinColumns = @JoinColumn(name = "product_product_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_tag_id")
    )
    private Set<ProductTags> productTags;


}