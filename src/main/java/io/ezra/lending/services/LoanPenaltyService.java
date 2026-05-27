package io.ezra.lending.services;

import io.ezra.lending.entities.LoanApplication;
import io.ezra.lending.entities.LoanTransaction;
import io.ezra.lending.repos.LoanApplicationRepo;
import io.ezra.lending.repos.LoanTransactionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.ezra.lending.services.LoanDisbursalService.generateTransactionReference;

@Service
@Slf4j
public class LoanPenaltyService {
    LoanApplicationRepo loanApplicationRepo;
    LoanDisbursalService loanDisbursalService;
    LoanTransactionRepo loanTransactionRepo;

    public void processCronActivities(){
        List<LoanApplication> loanApplications = loanApplicationRepo.findAll();

        LocalDate now = LocalDate.now();
        for(LoanApplication loanApplication: loanApplications){
            // Send notifications for any loans approaching repayment day, 2 days prior
            if(ChronoUnit.DAYS.between(now, loanApplication.getMaturityDate()) <= 2){
                // Publish notification to topic here
            }

            // Get defaulted loans
            if(ChronoUnit.DAYS.between(now, loanApplication.getMaturityDate()) < 0){
                applyPenalty(loanApplication);
            }
        }
    }

    public void applyPenalty(LoanApplication loanApplication){
        // Calculate penalty for each loan
        BigDecimal loanBalance = loanApplication.getLoanBalance();
        BigDecimal penaltyAmount = loanBalance.multiply(BigDecimal.valueOf(loanApplication.getPenaltyRate()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Apply penalty for each loan
        String transactionRefNumber = generateTransactionReference();
        LoanTransaction penaltyTransaction
                = loanDisbursalService.mapTransaction(loanApplication, "PENALTY", transactionRefNumber+"4",
                "", penaltyAmount, BigDecimal.ZERO, penaltyAmount, false);

        loanTransactionRepo.save(penaltyTransaction);
    }
}
