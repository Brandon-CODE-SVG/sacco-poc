package com.poc.saccopoc.repositories;

import com.poc.saccopoc.entities.Loan;
import com.poc.saccopoc.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatus(String status);
    List<Loan> findByMember(Member member);

}
