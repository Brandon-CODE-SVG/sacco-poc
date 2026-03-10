package com.poc.saccopoc.dtos;

import lombok.Data;

import java.util.List;
@Data
public class StatementResponse {
    public String memberName;
    public Double balance;
    public List<TransactionInfo> recentTransactions;


    public StatementResponse(String fullName, Double balance, List<TransactionInfo> history) {
        this.memberName = fullName;
        this.balance = balance;
        this.recentTransactions = history;
    }
}
