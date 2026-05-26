package io.ezra.lending.repos;

import io.ezra.lending.entities.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductRepo extends JpaRepository<LoanProduct, Integer> {
}
