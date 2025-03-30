package com.springboot.bankapp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.bankapp.model.Account;
import com.springboot.bankapp.model.Transaction;
import com.springboot.bankapp.repository.AccountRepository;
import com.springboot.bankapp.repository.TransactionRepository;

@Service
public class AccountService implements UserDetailsService{

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Account findAccountByUsername(String username){
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    
    public Account registerAccount(String username, String password){
        if(accountRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username already exists.");
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }

    public void deposit(Account account, BigDecimal amount){
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
            amount, "DEPOSIT", LocalDateTime.now(), account);

        transactionRepository.save(transaction);
    }

    public void withdraw(Account account, BigDecimal amount){
        if(account.getBalance().compareTo(amount) < 0){
            // .compareTo() returns -1 if first value is less than, 0 if equal, and 1 if greater
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
            amount, "WITHDRAW", LocalDateTime.now(), account);
        transactionRepository.save(transaction);
    }

}