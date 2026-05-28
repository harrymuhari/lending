package io.ezra.lending.api;

import io.ezra.lending.dtos.requests.LoanRepaymentReq;
import io.ezra.lending.dtos.responses.LoanRepaymentRes;
import io.ezra.lending.services.LoanRepaymentService;
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
public class LoanRepaymentController {
    public final LoanRepaymentService loanRepaymentService;

    @PostMapping("/repay")
    public LoanRepaymentRes repayLoan(@RequestBody LoanRepaymentReq loanRepaymentReq){
        return loanRepaymentService.repayLoan(loanRepaymentReq);
    }
}
