package io.ezra.lending.repos;

import io.ezra.lending.entities.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepo extends JpaRepository<LoanApplication, Integer> {
}
