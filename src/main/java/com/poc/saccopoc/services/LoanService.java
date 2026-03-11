package com.poc.saccopoc.services;

import com.poc.saccopoc.entities.Account;
import com.poc.saccopoc.entities.Loan;
import com.poc.saccopoc.entities.Member;
import com.poc.saccopoc.entities.Transaction;
import com.poc.saccopoc.repositories.AccountRepository;
import com.poc.saccopoc.repositories.LoanRepository;
import com.poc.saccopoc.repositories.MemberRepository;
import com.poc.saccopoc.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public Loan applyLoan(Long memberId, Double principalAmount){

        if (principalAmount == null || principalAmount <= 0) {
            throw new RuntimeException("TRANSACTION FAILED: Loan application amount must be greater than zero.");
        }

        //find the member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("Member Not Found"));
        // Create the loan Contract, we set the loan at 12% interest for the POC
        Loan newLoan = new Loan(principalAmount, 12.0, "APPROVED", member);

        return loanRepository.save(newLoan);
    }


    @Transactional
    public Transaction repayLoan(Long loanId, Long accountId, Double amount){

        if (amount == null || amount <= 0) {
            throw new RuntimeException("TRANSACTION FAILED: Repayment amount must be greater than zero.");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(()-> new RuntimeException("Loan Not Found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new RuntimeException("Account Not Found"));


        // Do they have enough money to repay the loan

        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient Balance");
        }

        // Deduct money from FOSA
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        //Reduce the loan principal amount
        loan.setPrincipalAmount(loan.getPrincipalAmount() - amount);

        //Auto close the loan if they repaid all of it

        if(loan.getPrincipalAmount() <= 0){
            loan.setPrincipalAmount(0.0);
            loan.setStatus("PAID IN FULL");
        }

        loanRepository.save(loan);

        //Create the receipt

        Transaction repaymentRecord = new Transaction(amount, "DEBIT_LOAN_REPAYMENT", account);
        return transactionRepository.save(repaymentRecord);
    }

    @Transactional
    public int simulateEndOfMonthInterest(){
        //Grab every entire active loan in the SACCO

        List<Loan> activeLoans = loanRepository.findByStatus("APPROVED");
        int count = 0;


        //Loop through them one by one
        for (Loan loan : activeLoans){

            // step 1 convert 12%(Annual interest) to 0.01(Monthly interest in decimal)

            double monthlyRate = (loan.getInterestRate() /100.0) / 12.0;

            //Step 2 calculate the exact interest for this specific month

            double InterestAmount =  loan.getPrincipalAmount() * monthlyRate;

            // Step 3 add the interest to their outstanding balance

            loan.setPrincipalAmount(loan.getPrincipalAmount() + InterestAmount);

            // Step 4 save it back to the database
            loanRepository.save(loan);
            count++;
        }

        //Return how many loans were just updated so that UI can show the bosses
        return count;
    }
}
