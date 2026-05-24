package io.ezra.lending.api;

import io.ezra.lending.services.TestService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class Test {
    public final TestService testService;

    @GetMapping("/test")
    public String test(){
        return testService.hello();
    }
}
