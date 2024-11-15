package dev.fredpena.lab.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    @Email
    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String email;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    @NotNull
    @Size(min = 1, max = 500)
    private String address;

    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String city;

    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String state;

    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String zipCode;

    @NotNull
    @NotNull
    @Size(min = 1, max = 100)
    private String country;

    @Size(max = 20)
    private String phone;

    @NotNull
    private Boolean newsletter;

    @Size(max = 15)
    private String username;

    @Size(max = 100)
    private String password;


}
