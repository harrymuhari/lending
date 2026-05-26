package io.ezra.lending.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LoanApplicationReq {
    BigDecimal loanAmount;
    Integer customerId;
    Integer loanProductId;
}
