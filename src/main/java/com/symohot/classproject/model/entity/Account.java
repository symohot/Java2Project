package com.symohot.classproject.model.entity;

import jakarta.persistence.*;

@Entity(name = "account")
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "accountNumber", nullable = false, unique = true)
    private String accountNumber;
    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "amount", nullable = false)
    private Long amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public Account() {
    }

    public Account(String accountNumber, String firstName, String lastName, Long amount, Status status) {
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.amount = amount;
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
