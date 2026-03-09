package com.poc.saccopoc.controllers;

import com.poc.saccopoc.dtos.LoanRequest;
import com.poc.saccopoc.entities.Loan;
import com.poc.saccopoc.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public Loan applyLoan(@RequestBody LoanRequest loanRequest) {
        return loanService.applyLoan(loanRequest.memberId, loanRequest.principalAmount);

    }
}
