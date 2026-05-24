package io.ezra.lending.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test")
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity {

    @Id
    Long id;

    String name;
}
