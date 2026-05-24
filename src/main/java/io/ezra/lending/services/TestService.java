package io.ezra.lending.services;

import io.ezra.lending.api.Test;
import io.ezra.lending.entities.TestEntity;
import io.ezra.lending.repos.TestRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TestService {
    public final TestRepo testRepo;

    public String hello(){
        Long id = 1L;
        Optional<TestEntity> test = testRepo.findById(id);

        return test.orElse(new TestEntity()).getName();
    }
}
