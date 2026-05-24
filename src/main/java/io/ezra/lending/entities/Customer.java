package io.ezra.lending.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "customers",
        schema = "defaultdb"
)
public class Customer {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "PENDING";

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender", length = 2)
    private String gender;

    @PrePersist
    public void prePersist() {

        if (createdOn == null) {
            createdOn = LocalDate.now();
        }

        if (status == null) {
            status = "PENDING";
        }
    }
}
