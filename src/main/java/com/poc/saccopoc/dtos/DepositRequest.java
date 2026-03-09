package com.poc.saccopoc.dtos;

import lombok.Data;

@Data
public class DepositRequest {
    public Long accountId;
    public Double amount;
}
