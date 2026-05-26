package io.ezra.lending.api;

import io.ezra.lending.dtos.responses.LoanReviewRes;
import io.ezra.lending.services.LoanReportsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class LoanReportsController {
    public final LoanReportsService loanReportsService;
    // Retrieve all PENDING loan applications for review, acceptance or rejection
    @GetMapping("/list")
    public LoanReviewRes listLoans(){
        return loanReportsService.listLoansToReview();
    }
}
