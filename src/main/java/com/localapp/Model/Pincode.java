package com.localapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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