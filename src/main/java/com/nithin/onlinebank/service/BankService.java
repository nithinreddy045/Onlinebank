package com.nithin.onlinebank.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nithin.onlinebank.model.Account;
import com.nithin.onlinebank.repository.AccountRepository;

@Service
public class BankService {

    @Autowired
    private AccountRepository accountRepository;

   
    public Account createAccount(Account account) {

       
        if (account.getBalance() < 0) {
            throw new RuntimeException("Initial balance cannot be negative");
        }

        Long generatedAccountNumber = generateUniqueAccountNumber();
        account.setAccountNo(generatedAccountNumber);

        return accountRepository.save(account);
    }

   
    public Account deposit(Long accountNo, double amount) {

        Account account = accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        account.setBalance(account.getBalance() + amount);

        return accountRepository.save(account);
    }

   
    public Account withdraw(Long accountNo, double amount) {

        Account account = accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);

        return accountRepository.save(account);
    }


    @Transactional
    public void transfer(Long fromAccount, Long toAccount, double amount) {

        if (fromAccount.equals(toAccount)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        Account sender = accountRepository.findById(fromAccount)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account receiver = accountRepository.findById(toAccount)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (!sender.isKYCVerified()) {
            throw new RuntimeException("Transfer failed: Sender account is NOT KYC Verified. Please complete KYC first.");
        }

        if (amount <= 0) {
            throw new RuntimeException("Transfer amount must be positive");
        }

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Transfer failed: Insufficient balance in sender account");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

   
    private Long generateUniqueAccountNumber() {

        Random random = new Random();
        Long accountNumber;

        do {
            accountNumber = 1000000000L +
                    (long) (random.nextDouble() * 9000000000L);
        } 
        while (accountRepository.existsById(accountNumber));

        return accountNumber;
    }
}