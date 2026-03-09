package com.poc.saccopoc.controllers;

import com.poc.saccopoc.dtos.DepositRequest;
import com.poc.saccopoc.dtos.TransferRequest;
import com.poc.saccopoc.entities.Transaction;
import com.poc.saccopoc.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/deposit")
    public Transaction deposit(@RequestBody DepositRequest depositRequest) {
        return transactionService.depositMoney(depositRequest.accountId, depositRequest.amount);
    }

    @PostMapping("/transfer")
    public List<Transaction> transfer(@RequestBody TransferRequest transferRequest) {
        return transactionService.transferMoney(
                transferRequest.getFromAccountId(),
                transferRequest.getToAccountId(),
                transferRequest.getAmount()
        );
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody DepositRequest depositRequest) {
        return transactionService.withdrawMoney(depositRequest.accountId, depositRequest.amount);
    }
}
