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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static io.ezra.lending.services.LoanDisbursalService.generateTransactionReference;

@Service
@AllArgsConstructor
public class LoanRepaymentService {
    final LoanApplicationRepo loanApplicationRepo;
    final LoanTransactionRepo loanTransactionRepo;
    final LoanRepaymentRepo loanRepaymentRepo;
    final LoanProductRepo loanProductRepo;
    final LoanDisbursalService loanDisbursalService;

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
            BigDecimal disbursedAmount = loanApplication.getDisbursedAmount();
            BigDecimal principalAmount = loanApplication.getPrincipalAmount();
            String transactionRefNumber = generateTransactionReference();

            // Check if loan is already defaulted, collect penalty
            if(loanApplication.getStatus().equalsIgnoreCase("DEFAULTED")){
               BigDecimal penaltyAmount = disbursedAmount.multiply(BigDecimal.valueOf(loanApplication.getPenaltyRate()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                disbursedAmount = disbursedAmount.subtract(penaltyAmount);
                LoanTransaction penaltyTransaction
                                = loanDisbursalService.mapTransaction(loanApplication, "PENALTY", transactionRefNumber+"1",
                                        "", BigDecimal.ZERO, penaltyAmount, penaltyAmount, false);

                loanTransactionRepo.save(penaltyTransaction);
            }

            // Collect fee
            LoanProduct loanProduct = loanProductRepo.findById(loanApplication.getLoanProduct().getProductCode()).orElseThrow();
            BigDecimal feeAmount = loanProduct.getLoanFee();
            disbursedAmount = disbursedAmount.subtract(feeAmount);
            LoanTransaction feeTransaction
                    = loanDisbursalService.mapTransaction(loanApplication, "FEE", transactionRefNumber+"1",
                    "", BigDecimal.ZERO, feeAmount, disbursedAmount, false);

            loanTransactionRepo.save(feeTransaction);

            // Collect interest
            BigDecimal interestAmount = principalAmount.multiply(BigDecimal.valueOf(loanApplication.getInterestRate()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            disbursedAmount = disbursedAmount.subtract(interestAmount);
            LoanTransaction interestTransaction
                    = loanDisbursalService.mapTransaction(loanApplication, "INTEREST", transactionRefNumber+"1",
                    "", BigDecimal.ZERO, interestAmount, disbursedAmount, false);

            loanTransactionRepo.save(interestTransaction);

            // Collect principal
            disbursedAmount = disbursedAmount.subtract(principalAmount);
            LoanTransaction principleTransaction
                    = loanDisbursalService.mapTransaction(loanApplication, "PRINCIPAL", transactionRefNumber+"1",
                    "", BigDecimal.ZERO, principalAmount, disbursedAmount, false);

            loanTransactionRepo.save(principleTransaction);

            // Mark loan as closed if balance is zero
            if(disbursedAmount.equals(0)){
                loanApplication.setStatus("CLOSED");
                loanApplicationRepo.save(loanApplication);
            }

            loanRepaymentRes.setStatusCode(00);
            loanRepaymentRes.setMessage("Loan repaid successfully");
        } catch(Exception ex){
            loanRepaymentRes.setStatusCode(98);
            loanRepaymentRes.setMessage("An exception occurred");
            ex.printStackTrace();
        }

        return loanRepaymentRes;
    }
}
