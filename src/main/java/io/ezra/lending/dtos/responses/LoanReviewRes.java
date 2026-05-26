package io.ezra.lending.dtos.responses;

import java.math.BigInteger;
import java.time.LocalDate;

public class LoanReviewRes {
    BigInteger loanReferenceId;
    Integer customerId;
    String customerName;
    Integer loanId;
    String loanName;
    String principalAmount;
    Integer interestRate;
    Integer penaltyRate;
    Integer tenure;
    String status;
    LocalDate createdOn;
}
