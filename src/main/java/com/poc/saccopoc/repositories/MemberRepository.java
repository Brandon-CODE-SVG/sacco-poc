package com.poc.saccopoc.repositories;

import com.poc.saccopoc.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByPhoneNumber(String phoneNumber);
}
