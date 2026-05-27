package io.ezra.lending.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "loan_applications",
        schema = "defaultdb"
)
public class LoanApplication {

    @Id
    @Column(name = "loan_reference_id", nullable = false)
    private BigInteger loanReferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            referencedColumnName = "id"
    )
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "loan_id",
            referencedColumnName = "product_code"
    )
    private LoanProduct loanProduct;

    @Column(name = "principal_amount", precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate")
    private Integer interestRate;

    @Column(name = "penalty_rate")
    private Integer penaltyRate;

    @Column(name = "tenure")
    private Integer tenure;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "defaulted_on")
    private LocalDate defaultedOn;

    @Column(name = "disbursed_amount")
    private BigDecimal disbursedAmount;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "loan_balance")
    private BigDecimal loanBalance;

    @PrePersist
    public void prePersist() {

        if (createdOn == null) {
            createdOn = LocalDate.now();
        }
    }
}