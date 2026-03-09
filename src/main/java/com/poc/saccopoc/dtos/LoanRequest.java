package com.poc.saccopoc.dtos;

import lombok.Data;

@Data
public class LoanRequest {
    public Long memberId;
    public Double principalAmount;
}
