package io.ezra.lending.repos;

import io.ezra.lending.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepo extends JpaRepository<TestEntity, Long> {
}
