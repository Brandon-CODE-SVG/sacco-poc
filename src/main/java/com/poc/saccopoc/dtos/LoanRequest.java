package com.poc.saccopoc.dtos;

import lombok.Data;

import java.util.List;

@Data
public class LoanRequest {
    public Long memberId;
    public Double principalAmount;
    private List<Long> guarantorIds;

}
