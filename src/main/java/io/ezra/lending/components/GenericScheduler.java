package io.ezra.lending.components;

import io.ezra.lending.services.LoanPenaltyService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GenericScheduler {
    public LoanPenaltyService loanPenaltyService;

    @Scheduled(cron = "0 1 * * * ?")
    public void run(){
        loanPenaltyService.processCronActivities();
    }
}
