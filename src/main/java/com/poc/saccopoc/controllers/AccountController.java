package com.poc.saccopoc.controllers;

import com.poc.saccopoc.dtos.StatementResponse;
import com.poc.saccopoc.dtos.TransactionInfo;
import com.poc.saccopoc.entities.Account;
import com.poc.saccopoc.repositories.AccountRepository;
import com.poc.saccopoc.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @GetMapping("/{id}/statement")
    public StatementResponse getMiniStatement(@PathVariable("id") Long accountId) {

        // Find the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Fetch the transaction history (limiting to the last 5)
        List<TransactionInfo> history = transactionRepository.findByAccountOrderByIdDesc(account)
                .stream()
                .limit(5) // Top 5 most recent
                .map(t -> new TransactionInfo(t.getAmount(), t.getTransactionType(), t.getTimestamp()))
                .toList();

        // Package it and send to the frontEnd
        return new StatementResponse(account.getMember().getFullName(), account.getBalance(), history);
    }
}
