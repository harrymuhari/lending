package io.ezra.lending.services;

import io.ezra.lending.dtos.requests.LoanRepaymentReq;
import io.ezra.lending.dtos.responses.LoanApplicationRes;
import io.ezra.lending.dtos.responses.LoanRepaymentRes;
import io.ezra.lending.entities.LoanApplication;
import io.ezra.lending.entities.LoanProduct;
import io.ezra.lending.entities.LoanRepayment;
import io.ezra.lending.entities.LoanTransaction;
import io.ezra.lending.repos.LoanApplicationRepo;
import io.ezra.lending.repos.LoanProductRepo;
import io.ezra.lending.repos.LoanRepaymentRepo;
import io.ezra.lending.repos.LoanTransactionRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static io.ezra.lending.services.LoanDisbursalService.generateTransactionReference;

@Service
@AllArgsConstructor
@Slf4j
public class LoanRepaymentService {
    final LoanApplicationRepo loanApplicationRepo;
    final LoanTransactionRepo loanTransactionRepo;
    final LoanRepaymentRepo loanRepaymentRepo;
    final LoanProductRepo loanProductRepo;
    final LoanDisbursalService loanDisbursalService;

    @Transactional
    public LoanRepaymentRes repayLoan(LoanRepaymentReq loanRepaymentReq){
        LoanRepaymentRes loanRepaymentRes = new LoanRepaymentRes();
        loanRepaymentRes.setStatusCode(99);
        loanRepaymentRes.setMessage("General failure");

        try {
            LoanRepayment loanRepayment = new LoanRepayment();
            loanRepayment.setTransactionReference(loanRepaymentReq.getTransactionReference());
            loanRepayment.setRepaymentAmount(loanRepaymentReq.getAmount());
            loanRepaymentRepo.save(loanRepayment);

            LoanApplication loanApplication = loanApplicationRepo.findById(loanRepaymentReq
                    .getLoanReferenceId()).orElseThrow();
            BigDecimal loanBalance = loanApplication.getLoanBalance();
            BigDecimal principalAmount = loanApplication.getPrincipalAmount();
            String transactionRefNumber = generateTransactionReference();

            // Check if loan is already defaulted, collect penalty
            if(loanApplication.getStatus().equalsIgnoreCase("DEFAULTED")){
               BigDecimal penaltyAmount = loanBalance.multiply(BigDecimal.valueOf(loanApplication.getPenaltyRate()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                loanBalance = loanBalance.subtract(penaltyAmount);
                LoanTransaction penaltyTransaction
                                = loanDisbursalService.mapTransaction(loanApplication, "PENALTY", transactionRefNumber+"1",
                                        "", BigDecimal.ZERO, penaltyAmount, penaltyAmount, false);

                loanTransactionRepo.save(penaltyTransaction);
            }

            // Collect fee
            LoanProduct loanProduct = loanProductRepo.findById(loanApplication.getLoanProduct().getProductCode()).orElseThrow();
            BigDecimal feeAmount = loanProduct.getLoanFee();
            loanBalance = loanBalance.subtract(feeAmount);
            LoanTransaction feeTransaction
                    = loanDisbursalService.mapTransaction(loanApplication, "FEE", transactionRefNumber+"2",
                    "", BigDecimal.ZERO, feeAmount, loanBalance, false);

            loanTransactionRepo.save(feeTransaction);

            // Collect interest
            BigDecimal interestAmount = principalAmount.multiply(BigDecimal.valueOf(loanApplication.getInterestRate()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            loanBalance = loanBalance.subtract(interestAmount);
            LoanTransaction interestTransaction
                    = loanDisbursalService.mapTransaction(loanApplication, "INTEREST", transactionRefNumber+"3",
                    "", BigDecimal.ZERO, interestAmount, loanBalance, false);

            loanTransactionRepo.save(interestTransaction);

            // Collect principal
            loanBalance = loanBalance.subtract(principalAmount);
            LoanTransaction principleTransaction
                    = loanDisbursalService.mapTransaction(loanApplication, "PRINCIPAL", transactionRefNumber+"4",
                    "", BigDecimal.ZERO, principalAmount, loanBalance, false);

            loanTransactionRepo.save(principleTransaction);

            // Mark loan as closed if balance is zero
            if(loanBalance.equals(0)){
                loanApplication.setStatus("CLOSED");
                loanApplicationRepo.save(loanApplication);
            }

            loanRepaymentRes.setStatusCode(00);
            loanRepaymentRes.setMessage("Loan repaid successfully");

            // Publish notification to topic here targeting applicant notifying them their loan repayment has been processed successfully
            // Publish notification to topic here targeting back office that a loan repayment was successful
            // Add more info in case the loan has been fully paid i.e status = CLOSED
        } catch(Exception ex){
            loanRepaymentRes.setStatusCode(98);
            loanRepaymentRes.setMessage("An exception occurred");
            ex.printStackTrace();
            log.info("Exception repaying loan {}", ex.getMessage());
        }

        return loanRepaymentRes;
    }
}
