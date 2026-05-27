package io.ezra.lending.api;

import io.ezra.lending.dtos.responses.LoanBalanceRes;
import io.ezra.lending.dtos.responses.LoanReviewRes;
import io.ezra.lending.dtos.responses.LoanStatementRes;
import io.ezra.lending.services.LoanReportsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // Check loan balance
    // Fetch balance from loan_applications, it's always updated after every transaction
    @GetMapping("/balance/{loanReferenceId}")
    public LoanBalanceRes fetchLoanBalance(@PathVariable String loanReferenceId){
        return loanReportsService.fetchLoanBalance(loanReferenceId);
    }

    // Check loan statement
    // Fetch from loan_transactions, include entries for principal, fees, interest and penalties
    // Order with most recent entries first
    @GetMapping("/statement/{loanReferenceId}")
    public LoanStatementRes fetchLoanStatement(@PathVariable String loanReferenceId){
        return loanReportsService.fetchLoanStatement(loanReferenceId);
    }

}
