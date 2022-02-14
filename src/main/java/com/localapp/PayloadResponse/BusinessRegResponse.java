package com.localapp.PayloadResponse;

import com.localapp.model.Business;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessRegResponse {
	private String message;
	private Business business;
	private String role;
}
