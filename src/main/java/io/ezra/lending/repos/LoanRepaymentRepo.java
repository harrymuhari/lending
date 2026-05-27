package io.ezra.lending.repos;

import io.ezra.lending.entities.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepaymentRepo extends JpaRepository<LoanRepayment, String> {
}
