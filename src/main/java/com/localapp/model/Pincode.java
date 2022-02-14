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
@Table(name = "pincodes_india")
@AllArgsConstructor
@NoArgsConstructor
public class Pincode{

    @Id
    @Column(name="pincode")
    int pincode;

    @Column(name = "district")
    String district;

    @Column(name = "statename")
    String statename;
}