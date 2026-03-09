package com.poc.saccopoc.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private  String accountType; //FOSA or BOSA
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    public Account(String fosa, double v, Member savedMember) {
        this.accountType = fosa;
        this.balance = v;
        this.member = savedMember;
    }

    public Account() {}
}
