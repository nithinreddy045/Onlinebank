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

    public Account() {}

    public Account(String holderName, double balance, boolean isKYCVerified) {
        this.holderName = holderName;
        this.balance = balance;
        this.isKYCVerified = isKYCVerified;
    }

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
}