package com.nithin.onlinebank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nithin.onlinebank.model.Account;
import com.nithin.onlinebank.service.BankService;
import com.nithin.onlinebank.repository.AccountRepository;

@Controller
public class BankController {

    @Autowired
    private BankService accountService;

    @Autowired
    private AccountRepository accountRepository;

    
    @GetMapping("/")
    public String dashboard(Model model) {

    long totalAccounts = accountRepository.count();

    double totalBalance = accountRepository.findAll()
            .stream()
            .mapToDouble(Account::getBalance)
            .sum();

    model.addAttribute("totalAccounts", totalAccounts);
    model.addAttribute("totalBalance", totalBalance);
    model.addAttribute("accounts", accountRepository.findAll());

    return "dashboard";
    }

    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("account", new Account());
        return "create-account";
    }

    @PostMapping("/create")
public String createAccount(@ModelAttribute Account account, Model model) {

    
    if (!account.getAadhaarNumber().matches("\\d{12}")) {
        model.addAttribute("error", "Aadhaar must be exactly 12 digits");
        model.addAttribute("account", account);
        return "create-account";
    }

   
    if (!account.getPhoneNumber().matches("\\d{10}")) {
        model.addAttribute("error", "Phone number must be exactly 10 digits");
        model.addAttribute("account", account);
        return "create-account";
    }

    
    if (account.getBalance() < 0) {
        model.addAttribute("error", "Initial balance cannot be negative");
        model.addAttribute("account", account);
        return "create-account";
    }

    
    Long accNo = generateAccountNumber();
    account.setAccountNo(accNo);

    
    account.setKYCVerified(true);

    accountRepository.save(account);

    model.addAttribute("success",
            "Account Created Successfully! Account Number: " + accNo);

    model.addAttribute("account", new Account());

    return "create-account";
}

    
    @GetMapping("/accounts")
    public String viewAccounts(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    
    @GetMapping("/deposit")
    public String showDepositPage(@RequestParam(required = false) Long accountNo,
                                  Model model) {

        model.addAttribute("accountNo", accountNo);
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountNo,
                          @RequestParam double amount,
                          Model model) {

        try {
            accountService.deposit(accountNo, amount);
            return "redirect:/accounts";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accountNo", accountNo);
            return "deposit";
        }
    }

   
    @GetMapping("/withdraw")
    public String showWithdrawPage(@RequestParam(required = false) Long accountNo,
                                   Model model) {

        model.addAttribute("accountNo", accountNo);
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long accountNo,
                           @RequestParam double amount,
                           Model model) {

        try {
            accountService.withdraw(accountNo, amount);
            model.addAttribute("success", "Withdrawal of ₹" + amount + " successful!");
            model.addAttribute("accountNo", accountNo);
            return "withdraw";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accountNo", accountNo);
            return "withdraw";
        }
    }

   
    @GetMapping("/transfer")
    public String showTransferPage() {
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccount,
                           @RequestParam Long toAccount,
                           @RequestParam double amount,
                           Model model) {

        try {
            accountService.transfer(fromAccount, toAccount, amount);
            model.addAttribute("success",
                    "Transfer of ₹" + amount + " from Account " + fromAccount
                            + " to Account " + toAccount + " was successful!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("fromAccount", fromAccount);
        model.addAttribute("toAccount", toAccount);
        return "transfer";
    }


    @GetMapping("/verify-kyc")
    public String showKycPage(@RequestParam Long accountNo, Model model) {

    Account account = accountRepository.findById(accountNo)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    model.addAttribute("account", account);
    return "verify-kyc";
    }

    @PostMapping("/verify-kyc")
    public String verifyKyc(@RequestParam Long accountNo,
                        @RequestParam String aadhaar,
                        @RequestParam String phone,
                        Model model) {

    Account account = accountRepository.findById(accountNo)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    if (!aadhaar.matches("\\d{12}")) {
        model.addAttribute("error", "Aadhaar must be exactly 12 digits");
        model.addAttribute("account", account);
        return "verify-kyc";
    }

    if (!phone.matches("\\d{10}")) {
        model.addAttribute("error", "Phone number must be exactly 10 digits");
        model.addAttribute("account", account);
        return "verify-kyc";
    }

    account.setAadhaarNumber(aadhaar);
    account.setPhoneNumber(phone);
    account.setKYCVerified(true);

    accountRepository.save(account);

    model.addAttribute("success", "KYC Verified Successfully!");
    model.addAttribute("account", account);

    return "verify-kyc";
    }
    private Long generateAccountNumber() {

    long min = 1000000000L;  
    long max = 9999999999L;  

    long number;

    do {
        number = min + (long)(Math.random() * (max - min));
    } while (accountRepository.existsById(number));

    return number;
    }


    @GetMapping("/personal-loan")
public String personalLoan() {
    return "personal-loan";
}

@GetMapping("/home-loan")
public String homeLoan() {
    return "home-loan";
}

@GetMapping("/platinum-card")
public String platinumCard() {
    return "platinum-card";
}

@GetMapping("/travel-card")
public String travelCard() {
    return "travel-card";
}
}