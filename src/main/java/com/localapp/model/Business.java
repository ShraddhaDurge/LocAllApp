package com.localapp.model;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


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


    @ManyToMany(fetch = FetchType.EAGER,  cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinTable(name = "business_pincodes",
            joinColumns = @JoinColumn(name = "business_business_id"),
            inverseJoinColumns = @JoinColumn(name = "pincodes_pinid")
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

    public Business(String businessName, String businessCategory, String address, String gstin, String license, Set<Pincode> pincodes) {
        this.businessName = businessName;
        this.businessCategory = businessCategory;
        this.address = address;
        this.gstin = gstin;
        this.license = license;
        this.pincodes = pincodes;
    }

    public Business(String businessName, String businessCategory, String address, String gstin, Set<Pincode> pincodes) {
        this.businessName = businessName;
        this.businessCategory = businessCategory;
        this.address = address;
        this.gstin = gstin;
        this.pincodes = pincodes;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Set<Pincode> getPincodes() {
        return pincodes;
    }

    public void setPincodes(Set<Pincode> pincodes) {
        this.pincodes = pincodes;
    }
}
