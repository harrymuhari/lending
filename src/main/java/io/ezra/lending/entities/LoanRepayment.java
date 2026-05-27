package io.ezra.lending.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_repaymens")
@Getter
@Setter
public class LoanRepayment {

    @Id
    @Column(name = "transaction_reference", length = 20)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "loan_reference_id",
            referencedColumnName = "loan_reference_id"
    )
    private LoanApplication loanApplication;

    @Column(name = "repayment_amount", precision = 19, scale = 2)
    private BigDecimal repaymentAmount;

    @Column(name = "repayment_date")
    private LocalDate repaymentDate;
}
