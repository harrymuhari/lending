package io.ezra.lending.services;

import io.ezra.lending.dtos.requests.LoanDisbursalReq;
import io.ezra.lending.dtos.responses.LoanDisbursalRes;
import io.ezra.lending.entities.LoanApplication;
import io.ezra.lending.entities.LoanProduct;
import io.ezra.lending.entities.LoanTransaction;
import io.ezra.lending.repos.LoanApplicationRepo;
import io.ezra.lending.repos.LoanProductRepo;
import io.ezra.lending.repos.LoanTransactionRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class LoanDisbursalService {
    public final LoanTransactionRepo loanTransactionRepo;
    public final LoanApplicationRepo loanApplicationRepo;
    public final LoanProductRepo loanProductRepo;

    @Transactional
    public LoanDisbursalRes disburseLoan(LoanDisbursalReq loanDisbursalReq){
        LoanDisbursalRes loanDisbursalRes = new LoanDisbursalRes();
        loanDisbursalRes.setStatusCode(99);
        loanDisbursalRes.setMessage("General failure");

        try {
            LoanApplication loanApplication = loanApplicationRepo.findById(loanDisbursalReq.getLoanReferenceId()).orElseThrow();

            if(!loanApplication.getStatus().equalsIgnoreCase("APPROVED")){
                loanDisbursalRes.setStatusCode(94);
                loanDisbursalRes.setMessage("Loan has not been approved");

                return loanDisbursalRes;
            }

            BigDecimal principal = loanApplication.getPrincipalAmount();
            BigDecimal interestAmount = principal.multiply(BigDecimal.valueOf(loanApplication.getInterestRate()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            LoanProduct loanProduct = loanProductRepo.findById(loanApplication.getLoanProduct().getProductCode()).orElseThrow();
            BigDecimal loanFee = loanProduct.getLoanFee();

            BigDecimal disbursedAmount = principal.add(interestAmount).add(loanFee);
            String transactionRefNumber = generateTransactionReference();

            // post principal
            LoanTransaction loanTransaction = mapTransaction(loanApplication, "PRINCIPAL", transactionRefNumber+"1",
                            "", principal, BigDecimal.ZERO, principal, false);

            loanTransactionRepo.save(loanTransaction);

            // post fee
            BigDecimal balanceAfterFee = principal.add(loanFee);
            LoanTransaction feeTransaction = mapTransaction(loanApplication, "FEE", transactionRefNumber+"2",
                                    "", loanFee, BigDecimal.ZERO, balanceAfterFee, false);
            loanTransactionRepo.save(feeTransaction);

            // post interest
            BigDecimal balanceAfterInterest = balanceAfterFee.add(interestAmount);
            LoanTransaction interestTransaction = mapTransaction(loanApplication, "INTEREST", transactionRefNumber+"3",
                                    "", interestAmount, BigDecimal.ZERO, balanceAfterInterest, false);
            loanTransactionRepo.save(interestTransaction);

            // if posting is successful, update loan status to ACTIVE
            loanApplication.setApprovalDate(LocalDate.now());
            loanApplication.setDisbursementDate(LocalDate.now());
            loanApplication.setStatus("ACTIVE");
            loanApplication.setDisbursedAmount(disbursedAmount);
            loanApplication.setApprovedBy(loanDisbursalReq.getApprovedBy());
            loanApplicationRepo.save(loanApplication);

            loanDisbursalRes.setStatusCode(00);
            loanDisbursalRes.setMessage("Loan disbursed successfully");
        } catch (Exception ex) {
            loanDisbursalRes.setStatusCode(98);
            loanDisbursalRes.setMessage("An exception occurred");
            log.info("Exception disbursing loan {}", ex.getMessage());
        }

        return loanDisbursalRes;
    }

    public LoanTransaction mapTransaction(LoanApplication loanApplication, String component,
                                          String transactionReference, String transactionType,
                                          BigDecimal debit, BigDecimal credit, BigDecimal balance,
                                          boolean reversed){
        LoanTransaction loanTransaction = new LoanTransaction();
        loanTransaction.setLoanReferenceId(loanApplication.getLoanReferenceId());
        loanTransaction.setTransactionReferenceId(transactionReference);
        loanTransaction.setTransactionType(transactionType);
        loanTransaction.setLoanComponent(component);
        loanTransaction.setDebit(debit);
        loanTransaction.setCredit(credit);
        log.info("Balance {}", balance);
        loanTransaction.setBalance(balance);
        loanTransaction.setTransactionDate(LocalDate.now());
        loanTransaction.setReversed(reversed);

        return loanTransaction;
    }

    public static String generateTransactionReference() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 10)
                .toUpperCase().replaceAll("\\-", "");
    }
}
