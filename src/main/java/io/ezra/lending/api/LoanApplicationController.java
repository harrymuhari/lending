package io.ezra.lending.api;

import io.ezra.lending.dtos.requests.LoanApplicationReq;
import io.ezra.lending.dtos.requests.LoanDisbursalReq;
import io.ezra.lending.dtos.requests.LoanScoreReq;
import io.ezra.lending.dtos.responses.LoanApplicationRes;
import io.ezra.lending.dtos.responses.LoanDisbursalRes;
import io.ezra.lending.dtos.responses.LoanScoreRes;
import io.ezra.lending.services.LoanApplicationService;
import io.ezra.lending.services.LoanDisbursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class LoanApplicationController {
    public final LoanApplicationService loanApplicationService;
    public final LoanDisbursalService loanDisbursalService;

    // Do scoring prior to application
    // Check loan amount applied for is higher/lower than the configured limits
    // Check if customer has any running loans, restrict on business rule e.g. no 2 similar active loans
    // Check if customer has defaulted currently/previously
    // Check with external providers e.g. credit bureaus to get score
    // Check using biodata?
    @PostMapping("/getScore")
    public LoanScoreRes getScore(@RequestBody LoanScoreReq loanScoreReq){
        return loanApplicationService.getScore(loanScoreReq);
    }


    @PostMapping("/apply")
    public LoanApplicationRes applyLoan(@RequestBody LoanApplicationReq loanApplicationRequest){
        return loanApplicationService.applyLoan(loanApplicationRequest);
    }

    // Do loan approval here after application, to disburse the loan
    @PostMapping("/disburse")
    public LoanDisbursalRes disburseLoan(@RequestBody LoanDisbursalReq loanDisbursalReq){
        return loanDisbursalService.disburseLoan(loanDisbursalReq);
    }
}
