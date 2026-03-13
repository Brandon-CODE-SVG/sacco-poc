package com.poc.saccopoc.controllers;

import com.poc.saccopoc.dtos.LoanRequest;
import com.poc.saccopoc.dtos.RepayRequest;
import com.poc.saccopoc.entities.Loan;
import com.poc.saccopoc.entities.Transaction;
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
        return loanService.applyLoan(
                loanRequest.getMemberId(),
                loanRequest.getPrincipalAmount(),
                loanRequest.getGuarantorIds()
        );

    }

    @PostMapping("/repay")
    public Transaction repayLoan(@RequestBody RepayRequest repayRequest) {
        return loanService.repayLoan(
                repayRequest.getLoanId(),
                repayRequest.getAccountId(),
                repayRequest.getAmount()
        );
    }

    @PostMapping("/simulate-interest")
    public String simulateInterestLoan() {
        int loansUpdated = loanService.simulateEndOfMonthInterest();

        return "{\"message\": \"EOM BATCH JOB COMPLETE: Applied monthly interest to " + loansUpdated + " active loans.\"}";
    }
}
