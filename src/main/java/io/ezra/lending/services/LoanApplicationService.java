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

            Optional<Customer> customer = customerRepo.findById(loanApplicationRequest.getCustomerId());
            String customerName = customer.map(Customer::getName).orElse("Customer");

            Optional<LoanProduct> loanProduct = loanProductRepo.findById(loanApplicationRequest.getLoanProductId());
            String loanName = loanProduct.map(LoanProduct::getProductName).orElse("Generic Loan");
            Integer interestRate = loanProduct.map(LoanProduct::getInterestRate).orElse(10);
            Integer penaltyRate = loanProduct.map(LoanProduct::getPenaltyRate).orElse(10);

            LoanApplication loanApplication = new LoanApplication();
            loanApplication.setLoanReferenceId(new BigInteger(loanReferenceId));
            loanApplication.setCustomerId(loanApplicationRequest.getCustomerId());
            loanApplication.setLoanId(loanApplicationRequest.getLoanProductId());
            loanApplication.setPrincipalAmount(loanApplicationRequest.getLoanAmount());
            loanApplication.setInterestRate(interestRate);
            loanApplication.setPenaltyRate(penaltyRate);

            LoanApplication savedLoanApplication = loanApplicationRepo.save(loanApplication);

            if (savedLoanApplication.getLoanReferenceId() != null) {
                loanApplicationRes.setStatusCode(00);
                loanApplicationRes.setMessage("Loan application successful");
            } else {
                loanApplicationRes.setStatusCode(97);
                loanApplicationRes.setMessage("Loan application failed");
            }
        } catch(Exception ex){
            loanApplicationRes.setStatusCode(98);
            loanApplicationRes.setMessage("An exception occurred");
            ex.printStackTrace();
        }

        return loanApplicationRes;
    }
}
