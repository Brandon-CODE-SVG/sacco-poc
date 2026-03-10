package com.poc.saccopoc.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TransactionInfo {

    public Double amount;
    public String type;
    public LocalDateTime timestamp;

    public TransactionInfo(Double amount, String transactionType, LocalDateTime timestamp) {
        this.amount = amount;
        this.type = transactionType;
        this.timestamp = timestamp;
    }
}
