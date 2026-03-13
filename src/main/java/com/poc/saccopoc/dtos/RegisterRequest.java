package com.poc.saccopoc.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String fullName;
    private String idNumber;
    private String phoneNumber;
    private String kraPin; // NEW
    private String tier;
}
