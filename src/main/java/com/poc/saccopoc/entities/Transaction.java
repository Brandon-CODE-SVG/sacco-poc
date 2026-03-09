package com.poc.saccopoc.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String transactionType;  // "DEBIT(money out) or CREDIT(Money in)"
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Transaction(double amount, String credit, Account account) {
        this.amount = amount;
        this.transactionType = credit;
        this.account = account;
    }

    public Transaction() {}
}
