package io.ezra.lending.components;

import io.ezra.lending.services.LoanPenaltyService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
@AllArgsConstructor
public class GenericScheduler {
    public final LoanPenaltyService loanPenaltyService;

    // Run every minute for testing
    @Scheduled(cron = "0 */1 * * * ?")
    public void run(){
        log.info("Running generic scheduler at {}", LocalDate.now());
        loanPenaltyService.processCronActivities();
    }
}
