package com.poc.saccopoc.services;

import com.poc.saccopoc.entities.Account;
import com.poc.saccopoc.entities.Member;
import com.poc.saccopoc.repositories.AccountRepository;
import com.poc.saccopoc.repositories.MemberRepository;
import com.poc.saccopoc.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Member registerMember(String fullName, String idNumber, String phoneNumber, String kraPin, String tier) {

        //Create the member with the new KRA PIN and tier.
        Member newMember = new Member(fullName, idNumber, phoneNumber, kraPin, tier);
        Member savedMember = memberRepository.save(newMember);

        //Automatically create a FOSA account for the new member
        Account fosaAccount = new Account("FOSA", 0.0, savedMember);
        accountRepository.save(fosaAccount);

        //Automatically Create a BOSA account for the new member
        Account bosaAccount = new Account("BOSA", 0.0, savedMember);
        accountRepository.save(bosaAccount);

        return savedMember;
    }
}
