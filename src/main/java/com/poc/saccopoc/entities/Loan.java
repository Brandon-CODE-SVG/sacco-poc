package com.poc.saccopoc.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double principalAmount;
    private Double interestRate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;



}
