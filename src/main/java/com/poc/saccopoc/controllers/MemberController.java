package com.poc.saccopoc.controllers;

import com.poc.saccopoc.entities.Member;
import com.poc.saccopoc.repositories.MemberRepository;
import com.poc.saccopoc.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public Member registerMember(@RequestBody Member newMember) {
        // The @RequestBody takes the JSON sent by the frontend and turns it into a Java Member object!
        return memberService.registerMember(newMember);
    }
}
