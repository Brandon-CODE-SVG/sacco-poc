package com.poc.saccopoc.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
     private String fullName;
     private String idNumber;
     private String phoneNumber;
     private LocalDate dateJoined;


}
