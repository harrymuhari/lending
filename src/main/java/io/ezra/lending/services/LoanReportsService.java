package io.ezra.lending.services;

import io.ezra.lending.dtos.responses.LoanReviewEntity;
import io.ezra.lending.dtos.responses.LoanReviewRes;
import io.ezra.lending.entities.LoanApplication;
import io.ezra.lending.repos.LoanApplicationRepo;
import io.ezra.lending.repos.LoanTransactionRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanReportsService {
    public final LoanApplicationRepo loanApplicationRepo;

    @Transactional
    public LoanReviewRes listLoansToReview(){
        LoanReviewRes loanReviewRes = new LoanReviewRes();
        loanReviewRes.setStatusCode(99);
        loanReviewRes.setMessage("General failure");

        try {
            List<LoanApplication> loanApplications = loanApplicationRepo.findAll();
            if (!loanApplications.isEmpty()) {
                loanReviewRes.setStatusCode(00);
                loanReviewRes.setMessage("Loans fetched successfully");

                List<LoanReviewEntity> reviewEntities = new ArrayList<>();
                for(LoanApplication application: loanApplications){
                    LoanReviewEntity entity = new LoanReviewEntity();
                    entity.setLoanReferenceId(application.getLoanReferenceId());
                    entity.setCustomerId(application.getCustomer().getId());
                    entity.setCustomerName(application.getCustomer().getName());
                    entity.setLoanId(application.getLoanProduct().getProductCode());
                    entity.setLoanName(application.getLoanProduct().getProductName());
                    entity.setPrincipalAmount(application.getPrincipalAmount());
                    entity.setInterestRate(application.getInterestRate());
                    entity.setPenaltyRate(application.getPenaltyRate());
                    entity.setTenure(application.getTenure());
                    entity.setStatus(application.getStatus());
                    entity.setCreatedOn(application.getCreatedOn());

                    reviewEntities.add(entity);
                }

                loanReviewRes.setEntities(reviewEntities);
            } else {
                loanReviewRes.setStatusCode(96);
                loanReviewRes.setMessage("No loans found");
            }
        } catch (Exception ex) {
            loanReviewRes.setStatusCode(98);
            loanReviewRes.setMessage("An exception occurred");
            ex.printStackTrace();
        }

        return loanReviewRes;
    }
}
