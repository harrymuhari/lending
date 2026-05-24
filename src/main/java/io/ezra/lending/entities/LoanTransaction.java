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
        name = "loan_transactions",
        schema = "defaultdb"
)
public class LoanTransaction {

    @Id
    @Column(name = "transaction_reference_id", nullable = false, length = 20)
    private String transactionReferenceId;

    @Column(name = "loan_reference_id", nullable = false)
    private Integer loanReferenceId;

    @Column(name = "transaction_type", length = 20)
    private String transactionType;

    @Column(name = "loan_component", length = 50)
    private String loanComponent;

    @Column(name = "debit", precision = 19, scale = 2)
    private BigDecimal debit;

    @Column(name = "credit", precision = 19, scale = 2)
    private BigDecimal credit;

    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "transacion_date")
    private LocalDate transactionDate;

    @Column(name = "reversed")
    private Boolean reversed = false;

    @PrePersist
    public void prePersist() {

        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }

        if (reversed == null) {
            reversed = false;
        }
    }
}
