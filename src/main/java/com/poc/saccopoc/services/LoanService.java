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


    @Transactional
    public Loan applyLoan(Long memberId, Double principalAmount, List<Long> guarantorIds) {
        //  Check for negative or zero amounts
        if (principalAmount == null || principalAmount <= 0) {
            throw new RuntimeException("TRANSACTION FAILED: Loan application amount must be greater than zero.");
        }

        //  Find the Member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("Member Not Found"));

        //  Compliance Check for Minors
        if ("JUNIOR".equalsIgnoreCase(member.getTier())) {
            throw new RuntimeException("COMPLIANCE VIOLATION: Junior members are not permitted to take out credit facilities.");
        }

        //  Find the Borrower's BOSA Account
        List<Account> accounts = accountRepository.findByMember(member);
        Account bosaAccount = accounts.stream()
                .filter(acc -> "BOSA".equalsIgnoreCase(acc.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CRITICAL ERROR: Member does not have a BOSA Share Capital account."));

        //  Minimum Shares Bouncer
        if (bosaAccount.getBalance() < 5000.0) {
            throw new RuntimeException("LOAN DENIED: Insufficient Share Capital. Minimum required is KES 5,000. Current balance: KES " + bosaAccount.getBalance());
        }

        // The Multiplier Bouncer (3x Limit)
        List<Loan> existingLoans = loanRepository.findByMember(member);
        double totalOutstandingDebt = existingLoans.stream()
                .mapToDouble(Loan::getPrincipalAmount)
                .sum();
        double totalRequestedExposure = totalOutstandingDebt + principalAmount;
        double maxLoanLimit = bosaAccount.getBalance() * 3.0;

        if (totalRequestedExposure > maxLoanLimit) {
            throw new RuntimeException(String.format(
                    "LOAN DENIED: Total exposure exceeds 3x Share Capital limit. Max Allowed: KES %.2f | Current Debt: KES %.2f | Requested: KES %.2f",
                    maxLoanLimit, totalOutstandingDebt, principalAmount
            ));
        }

        // THE GUARANTOR BOUNCER
        if (guarantorIds == null || guarantorIds.isEmpty()) {
            throw new RuntimeException("LOAN DENIED: Unsecured loans are strictly prohibited. You must provide at least one guarantor.");
        }

        double totalGuarantorCapacity = 0.0;

        for (Long gId : guarantorIds) {
            if (gId.equals(memberId)) {
                throw new RuntimeException("COMPLIANCE VIOLATION: A member cannot act as a guarantor for their own loan.");
            }
            Member guarantor = memberRepository.findById(gId)
                    .orElseThrow(() -> new RuntimeException("GUARANTOR REJECTED: Member ID " + gId + " does not exist."));

            Account guarantorBosa = accountRepository.findByMember(guarantor).stream()
                    .filter(acc -> "BOSA".equalsIgnoreCase(acc.getAccountType()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CRITICAL ERROR: Guarantor ID " + gId + " does not have a BOSA account."));

            totalGuarantorCapacity += guarantorBosa.getBalance();
        }

        // Calculate the Unsecured Risk
        // The SACCO only cares about the money not covered by your own shares!
        double unsecuredRisk = principalAmount - bosaAccount.getBalance();

        // If the loan is smaller than your own shares, unsecured risk is 0.
        // Otherwise, guarantors must cover the difference.
        if (unsecuredRisk > 0 && totalGuarantorCapacity < unsecuredRisk) {
            throw new RuntimeException(String.format(
                    "LOAN DENIED: Insufficient guarantor capacity. Unsecured Risk: KES %.2f | Guarantors Combined BOSA: KES %.2f",
                    unsecuredRisk, totalGuarantorCapacity
            ));
        }

        // 8. If they pass ALL regulatory checks, originate the loan contract!
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
