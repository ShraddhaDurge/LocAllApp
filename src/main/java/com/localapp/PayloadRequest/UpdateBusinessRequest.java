package com.localapp.PayloadRequest;


import com.localapp.Model.Pincode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBusinessRequest {
    String businessName;
    String businessCategory;
    String address;
    Set<Pincode> pincodes;

}
