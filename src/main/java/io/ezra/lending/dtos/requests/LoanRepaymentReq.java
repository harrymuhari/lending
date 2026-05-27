package io.ezra.lending.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepaymentReq {
    BigInteger loanReferenceId;
    BigDecimal amount;
    String transactionReference;
}
