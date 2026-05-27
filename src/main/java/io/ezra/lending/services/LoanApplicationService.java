package io.ezra.lending.services;

import io.ezra.lending.dtos.requests.LoanApplicationReq;
import io.ezra.lending.dtos.responses.LoanApplicationRes;
import io.ezra.lending.entities.Customer;
import io.ezra.lending.entities.LoanApplication;
import io.ezra.lending.entities.LoanProduct;
import io.ezra.lending.repos.CustomerRepo;
import io.ezra.lending.repos.LoanApplicationRepo;
import io.ezra.lending.repos.LoanProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class LoanApplicationService {
    public final LoanApplicationRepo loanApplicationRepo;
    public final CustomerRepo customerRepo;
    public final LoanProductRepo loanProductRepo;

    public LoanApplicationRes applyLoan(LoanApplicationReq loanApplicationRequest){
        LoanApplicationRes loanApplicationRes = new LoanApplicationRes();
        loanApplicationRes.setStatusCode(99);
        loanApplicationRes.setMessage("General failure");

        try {
            // Enrich request, add loan name and customer name from loan product and customer id resp
            Random rand = new Random();
            String loanReferenceId = loanApplicationRequest.getLoanProductId() + "" +
                    loanApplicationRequest.getCustomerId() + "" +
                    rand.nextInt(2);

            Customer customer = customerRepo.findById(loanApplicationRequest.getCustomerId()).orElseThrow();
            String customerName = customer.getName();

            LoanProduct loanProduct = loanProductRepo.findById(loanApplicationRequest.getLoanProductId()).orElseThrow();
            String loanName = loanProduct.getProductName();
            Integer interestRate = loanProduct.getInterestRate();
            Integer penaltyRate = loanProduct.getPenaltyRate();

            LoanApplication loanApplication = new LoanApplication();
            loanApplication.setLoanReferenceId(new BigInteger(loanReferenceId));
            loanApplication.setCustomer(customer);
            loanApplication.setLoanProduct(loanProduct);
            loanApplication.setPrincipalAmount(loanApplicationRequest.getLoanAmount());
            loanApplication.setInterestRate(interestRate);
            loanApplication.setPenaltyRate(penaltyRate);

            LocalDate maturityDate = LocalDate.now().plusMonths(loanProduct.getTenure());
            loanApplication.setMaturityDate(maturityDate);

            LoanApplication savedLoanApplication = loanApplicationRepo.save(loanApplication);

            if (savedLoanApplication.getLoanReferenceId() != null) {
                loanApplicationRes.setStatusCode(00);
                loanApplicationRes.setMessage("Loan application successful");

                // Publish notification to topic here targeting back office to review the loan
            } else {
                loanApplicationRes.setStatusCode(97);
                loanApplicationRes.setMessage("Loan application failed");
            }
        } catch(Exception ex){
            loanApplicationRes.setStatusCode(98);
            loanApplicationRes.setMessage("An exception occurred");
            log.info("Exception applying loan {}", ex.getMessage());
        }

        return loanApplicationRes;
    }
}
