package com.nithin.onlinebank.model;

import jakarta.persistence.*;

@Entity
public class Account {

    @Id
    private Long accountNo;

    private String holderName;
    private double balance;
    private boolean isKYCVerified = false;
    private String aadhaarNumber;
    private String phoneNumber;

    private String username;
    private String pin;

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public Account() {}

    public Long getAccountNo() { return accountNo; }
    public void setAccountNo(Long accountNo) { this.accountNo = accountNo; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean isKYCVerified() { return isKYCVerified; }
    public void setKYCVerified(boolean KYCVerified) { isKYCVerified = KYCVerified; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}