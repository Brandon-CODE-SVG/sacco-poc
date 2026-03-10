package com.poc.saccopoc.dtos;

import lombok.Data;

@Data
public class RepayRequest {

    private Long loanId;
    private Long accountId;
    private Double amount;
}
