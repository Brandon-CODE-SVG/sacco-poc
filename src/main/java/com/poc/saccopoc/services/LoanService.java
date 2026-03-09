package com.poc.saccopoc.services;

import com.poc.saccopoc.entities.Loan;
import com.poc.saccopoc.entities.Member;
import com.poc.saccopoc.repositories.LoanRepository;
import com.poc.saccopoc.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;


    public Loan applyLoan(Long memberId, Double principalAmount){
        //find the member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("Member Not Found"));
        // Create the loan Contract, we set the loan at 12% interest for the POC
        Loan newLoan = new Loan(principalAmount, 12.0, "APPROVED", member);

        return loanRepository.save(newLoan);
    }
}
