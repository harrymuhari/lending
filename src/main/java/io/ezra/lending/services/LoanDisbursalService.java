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
            Optional<LoanApplication> loanApplication = loanApplicationRepo.findById(loanDisbursalReq.getLoanReferenceId());

            if(!loanApplication.get().getStatus().equalsIgnoreCase("APPROVED")){
                loanDisbursalRes.setStatusCode(94);
                loanDisbursalRes.setMessage("Loan has not been approved");

                return loanDisbursalRes;
            }

            BigDecimal principal = loanApplication.get().getPrincipalAmount();
            BigDecimal interestAmount = principal.multiply(BigDecimal.valueOf(loanApplication.get().getInterestRate()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            Optional<LoanProduct> loanProduct = loanProductRepo.findById(loanApplication.get().getLoanProduct().getProductCode());
            BigDecimal loanFee = loanProduct.get().getLoanFee();

            BigDecimal disbursedAmount = principal.add(interestAmount).add(loanFee);
            String transactionRefNumber = generateTransactionReference();

            // post principal
            LoanTransaction loanTransaction = loanApplication.map(
                    application -> mapTransaction(application, "PRINCIPAL", transactionRefNumber+"1",
                            "", principal, BigDecimal.ZERO, principal, false))
                    .orElseThrow(() -> new RuntimeException("No loan application found"));
            loanTransactionRepo.save(loanTransaction);

            // post fee
            BigDecimal balanceAfterFee = principal.add(loanFee);
            LoanTransaction feeTransaction = loanApplication.map(
                            application -> mapTransaction(application, "FEE", transactionRefNumber+"2",
                                    "", loanFee, BigDecimal.ZERO, balanceAfterFee, false))
                    .orElseThrow(() -> new RuntimeException("No loan application found"));
            loanTransactionRepo.save(feeTransaction);

            // post interest
            BigDecimal balanceAfterInterest = balanceAfterFee.add(interestAmount);
            LoanTransaction interestTransaction = loanApplication.map(
                            application -> mapTransaction(application, "INTEREST", transactionRefNumber+"3",
                                    "", interestAmount, BigDecimal.ZERO, balanceAfterInterest, false))
                    .orElseThrow(() -> new RuntimeException("No loan application found"));
            loanTransactionRepo.save(interestTransaction);

            // if posting is successful, update loan status to ACTIVE
            LoanApplication currentLoan = loanApplication.get();
            currentLoan.setApprovalDate(LocalDate.now());
            currentLoan.setDisbursementDate(LocalDate.now());
            currentLoan.setStatus("ACTIVE");
            currentLoan.setDisbursedAmount(disbursedAmount);
            currentLoan.setApprovedBy(loanDisbursalReq.getApprovedBy());
            loanApplicationRepo.save(currentLoan);

            loanDisbursalRes.setStatusCode(00);
            loanDisbursalRes.setMessage("Loan disbursed successfully");
        } catch (Exception ex) {
            loanDisbursalRes.setStatusCode(98);
            loanDisbursalRes.setMessage("An exception occurred");
            ex.printStackTrace();
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
