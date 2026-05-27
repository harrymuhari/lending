package io.ezra.lending.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
public class LoanDisbursalReq {
    BigInteger loanReferenceId;
    String approvedBy;
}
