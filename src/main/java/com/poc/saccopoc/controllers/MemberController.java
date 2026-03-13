package com.poc.saccopoc.controllers;

import com.poc.saccopoc.dtos.Member360Response;
import com.poc.saccopoc.dtos.RegisterRequest;
import com.poc.saccopoc.entities.Account;
import com.poc.saccopoc.entities.Member;
import com.poc.saccopoc.repositories.AccountRepository;
import com.poc.saccopoc.repositories.MemberRepository;
import com.poc.saccopoc.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    @PostMapping("/register")
    public Member registerMember(@RequestBody RegisterRequest registerRequest) {
        return memberService.registerMember(
                registerRequest.getFullName(),
                registerRequest.getIdNumber(),
                registerRequest.getPhoneNumber(),
                registerRequest.getKraPin(),
                registerRequest.getTier()
        );
    }


    @GetMapping("/{id}/360")
    public Member360Response getMember360(@PathVariable("id") Long memberId) {

        // Find the member

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        //Fetch all their accounts

        List<Account> accounts = accountRepository.findByMember(member);

        Double fosaBalance = 0.0;
        Long fosaId = null;
        Double bosaBalance = 0.0;
        Long bosaId = null;

        // SortOut which account is which

        for (Account acc : accounts) {
            if ("FOSA".equalsIgnoreCase(acc.getAccountType())) {
                fosaBalance = acc.getBalance();
                fosaId = (long) acc.getId();
            } else if ("BOSA".equalsIgnoreCase(acc.getAccountType())) {
                bosaBalance = acc.getBalance();
                bosaId = (long) acc.getId();
            }
        }

        //Ship the data to the frontEnd
        return new Member360Response(member.getFullName(), member.getTier(), fosaBalance, fosaId, bosaBalance, bosaId);
    }
}
