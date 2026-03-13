package com.poc.saccopoc.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member360Response {
    private String memberName;
    private String tier;
    private Double fosaBalance;
    private Long fosaAccountId;
    private Double bosaBalance;
    private Long bosaAccountId;
}
