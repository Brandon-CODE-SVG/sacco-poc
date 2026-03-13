package com.poc.saccopoc.services;

import com.poc.saccopoc.entities.Account;
import com.poc.saccopoc.entities.Transaction;
import com.poc.saccopoc.repositories.AccountRepository;
import com.poc.saccopoc.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public Transaction depositMoney(Long accountId, Double amount) {

        //A bouncer check
        if (amount == null || amount <= 0) {
            throw new RuntimeException("TRANSACTION FAILED: Deposit amount must be greater than zero.");
        }

        // Find the account, if it doesn't exist, throw an error
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new RuntimeException("Account not found"));

        //Add the money to the balance
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        // Create the Transaction "Receipt" (Audit trail)
        Transaction  depositRecord = new Transaction(amount, "CREDIT", account);
        return transactionRepository.save(depositRecord);
    }


    @Transactional
    public List<Transaction> transferMoney(Long fromAccountId, Long toAccountId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("TRANSACTION FAILED: Transfer amount must be greater than zero.");
        }

        //prevent transferring money to your own account
        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("TRANSACTION FAILED: Cannot transfer money to the same account.");
        }

        //Find both accounts
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(()-> new RuntimeException(" Sender Account not found"));

        if ("BOSA".equalsIgnoreCase(fromAccount.getAccountType())) {
            throw new RuntimeException("COMPLIANCE VIOLATION: You cannot transfer funds out of a Share Capital (BOSA) account.");
        }


        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(()-> new RuntimeException("Receiver Account not found"));



        //Business rule: check if they have enough money
        if(fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        //Move the money in the account balances
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Double Entry Proof!(Two receipt created at the exact same time)
        Transaction  transferRecord = new Transaction(amount, "DEBIT", fromAccount);
        Transaction depositRecord = new Transaction(amount, "CREDIT", toAccount);

        // Save both of them simultaneously

        return transactionRepository.saveAll(Arrays.asList(depositRecord, transferRecord));
    }

    @Transactional
    public Transaction withdrawMoney(Long accountId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("TRANSACTION FAILED: Withdrawal amount must be greater than zero.");
        }
        //Find the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new RuntimeException("Account not found"));

        if("BOSA".equalsIgnoreCase(account.getAccountType())){
            throw new RuntimeException("COMPLIANCE VIOLATION: Share Capital (BOSA) is locked and cannot be withdrawn.");
        }

        //The golden Rule: Check if they have enough Money!

        if(account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds! Transaction denied");

        }

        // Deduct the money

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        //Create the DEBIT receipt
        Transaction withdrawalRecord = new Transaction(amount, "DEBIT", account);
        return transactionRepository.save(withdrawalRecord);
    }
}
