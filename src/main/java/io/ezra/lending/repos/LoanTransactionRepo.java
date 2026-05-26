package io.ezra.lending.repos;

import io.ezra.lending.entities.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanTransactionRepo extends JpaRepository<LoanTransaction, String> {
}
