package com.localapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Entity
@Table(name = "pincodes")
@AllArgsConstructor
@NoArgsConstructor
public class Pincode{

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name="pinid")
    int pinid;

    @Column(name = "pincode")
    int pincode;

    public int getPinid() {
        return pinid;
    }

    public void setPinid(int pinid) {
        this.pinid = pinid;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public Pincode(int pincode) {
        this.pincode = pincode;
    }
}