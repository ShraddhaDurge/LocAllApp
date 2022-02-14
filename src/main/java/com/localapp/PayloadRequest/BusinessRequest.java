package com.localapp.PayloadRequest;


import com.localapp.model.Pincode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessRequest {

    String businessName;
    String businessCategory;
    String address;
    Set<Integer> pincodes;
    String gstin;

}
