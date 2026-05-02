package com.symohot.classproject.model.repository;

import com.symohot.classproject.model.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class AccountRepository {
    public void create(Account account, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(account);
        tx.commit();
    }
    public Account read(String accountNumber, EntityManager em) {
        Account account = null;
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        account = em.find(Account.class, accountNumber);
        tx.commit();
        return account;
    }
    public void withdraw(String accountNumber, Long amount,EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Account account = em.find(Account.class, accountNumber);
        account.setAmount(account.getAmount()-amount);
        em.merge(account);
        tx.commit();
    }
    public void deposit(String accountNumber, Long amount,EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Account account = em.find(Account.class, accountNumber);
        account.setAmount(account.getAmount()+amount);
        em.merge(account);
        tx.commit();
    }
}
