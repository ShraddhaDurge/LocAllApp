package com.localapp.Model;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Entity
@Table(name = "business")
@AllArgsConstructor
@NoArgsConstructor
public class Business{

        @GeneratedValue(strategy=GenerationType.IDENTITY)
        @Id
        @Column(name = "business_id")
        int business_id ;

        @Column(name = "bname")
        String businessName;

        @Column(name = "bcategory")
        String businessCategory;

        @Column(name = "address")
        String address;

        @Column(name = "gstin")
        String gstin;

        @Column(name = "license")
        String license;

        @Column(name = "status")
        String status;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.EAGER,  cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinTable(name = "business_deliver_pincodes",
            joinColumns = @JoinColumn(name = "business_business_id"),
            inverseJoinColumns = @JoinColumn(name = "pincodes_india_pincode")
    )
    private Set<Pincode> pincodes;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_business",
            joinColumns = @JoinColumn(name = "business_business_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "vendor_product",
            inverseJoinColumns = @JoinColumn(name = "product_product_id"),
            joinColumns = @JoinColumn(name = "business_business_id")
    )
    private Set<Product> products;

    public Business(String businessName, String businessCategory, String address, String gstin, String license, Set<Pincode> pincodes) {
        this.businessName = businessName;
        this.businessCategory = businessCategory;
        this.address = address;
        this.gstin = gstin;
        this.license = license;
        this.pincodes = pincodes;

    }

    public Business(String businessName, String businessCategory, String address, String gstin, Set<Pincode> pincodes, String status) {
        this.businessName = businessName;
        this.businessCategory = businessCategory;
        this.address = address;
        this.gstin = gstin;
        this.pincodes = pincodes;
        this.status = status;
    }
}
