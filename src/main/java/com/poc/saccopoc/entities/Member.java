package com.poc.saccopoc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
     private String fullName;

    @Column(nullable = false, unique = true)
     private String idNumber;

    @Column(nullable = false)
     private String phoneNumber;

     private LocalDate dateJoined;

    @Column(unique = true)
    private String kraPin;

    @Column(nullable = false)
    private String tier; // ORDINARY, JUNIOR, or COOPERATE

    public Member(String fullName, String idNumber, String phoneNumber, String kraPin, String tier) {
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.kraPin = kraPin;
        this.tier = tier;
    }


}
