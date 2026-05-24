package io.ezra.lending.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "loan_products",
        schema = "defaultdb"
)
public class LoanProduct {

    @Id
    @Column(name = "product_code", nullable = false)
    private Integer productCode;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "min_amount", precision = 19, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", precision = 19, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "interest_rate")
    private Integer interestRate;

    @Column(name = "penalty_rate")
    private Integer penaltyRate;

    @Column(name = "tenure")
    private Integer tenure;

    @Column(name = "default_after")
    private Integer defaultAfter;

    @Column(name = "created_on")
    private LocalDate createdOn;

    @Column(name = "loan_fee", precision = 19, scale = 2)
    private BigDecimal loanFee;

    @PrePersist
    public void prePersist() {
        if (createdOn == null) {
            createdOn = LocalDate.now();
        }
        if (active == null) {
            active = true;
        }
    }
}
