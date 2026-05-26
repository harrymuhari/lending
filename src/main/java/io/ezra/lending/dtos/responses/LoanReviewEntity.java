package io.ezra.lending.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanReviewEntity {
    BigInteger loanReferenceId;
    Integer customerId;
    String customerName;
    Integer loanId;
    String loanName;
    BigDecimal principalAmount;
    Integer interestRate;
    Integer penaltyRate;
    Integer tenure;
    String status;
    LocalDate createdOn;
}
