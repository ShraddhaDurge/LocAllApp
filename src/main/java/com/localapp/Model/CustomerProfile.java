package com.localapp.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "customerProfile")
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfile {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    int id;

    @Column(name = "billingAddress")
    String billingAddress;
    @Column(name = "shippingAddress")
    String shippingAddress;

    @Column(name = "shippingPincode")
    String shippingPincode;

    @Column(name = "billingPincode")
    String billingPincode;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_profile",
            joinColumns = @JoinColumn(name = "customer_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User user;


}
