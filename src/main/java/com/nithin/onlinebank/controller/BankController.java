package com.nithin.onlinebank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nithin.onlinebank.model.Account;
import com.nithin.onlinebank.service.BankService;
import com.nithin.onlinebank.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class BankController {

    @Autowired
    private BankService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private boolean notLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") == null;
    }

    // ================= CREATE ACCOUNT =================

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("account", new Account());
        return "create-account";
    }

    @PostMapping("/create")
    public String createAccount(@ModelAttribute Account account,
                                Model model) {

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

        if (!account.getPin().matches("\\d{4}")) {
            model.addAttribute("error", "PIN must be exactly 4 digits");
            model.addAttribute("account", account);
            return "create-account";
        }

        account.setAccountNo(generateAccountNumber());
        account.setKYCVerified(true);

        accountRepository.save(account);

        model.addAttribute("success",
                "Account Created Successfully! Account Number: " + account.getAccountNo());

        model.addAttribute("account", new Account());

        return "create-account";
    }

    // ================= VIEW BALANCE =================

    @GetMapping("/view-balance")
    public String showViewBalancePage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "view-balance";
    }

    @PostMapping("/view-balance")
    public String checkBalance(@RequestParam Long accountNo,
                               @RequestParam String pin,
                               Model model,
                               HttpSession session) {

        if (notLoggedIn(session)) return "redirect:/";

        Account account = accountRepository.findByAccountNoAndPin(accountNo, pin);

        if (account == null) {
            model.addAttribute("error", "Incorrect Account Number or PIN");
            return "view-balance";
        }

        model.addAttribute("balance", account.getBalance());
        return "view-balance";
    }

    // ================= DEPOSIT =================

    @GetMapping("/deposit")
    public String showDepositPage(HttpSession session,
                                  @RequestParam(required = false) Long accountNo,
                                  Model model) {

        if (notLoggedIn(session)) return "redirect:/";

        model.addAttribute("accountNo", accountNo);
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountNo,
                          @RequestParam double amount,
                          Model model,
                          HttpSession session) {

        if (notLoggedIn(session)) return "redirect:/";

        try {
            accountService.deposit(accountNo, amount);
            model.addAttribute("success", "Deposit successful!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("accountNo", accountNo);
        return "deposit";
    }

    // ================= WITHDRAW =================

    @GetMapping("/withdraw")
    public String showWithdrawPage(HttpSession session,
                                   @RequestParam(required = false) Long accountNo,
                                   Model model) {

        if (notLoggedIn(session)) return "redirect:/";

        model.addAttribute("accountNo", accountNo);
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long accountNo,
                           @RequestParam double amount,
                           Model model,
                           HttpSession session) {

        if (notLoggedIn(session)) return "redirect:/";

        try {
            accountService.withdraw(accountNo, amount);
            model.addAttribute("success", "Withdrawal successful!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("accountNo", accountNo);
        return "withdraw";
    }

    // ================= TRANSFER =================

    @GetMapping("/transfer")
    public String showTransferPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccount,
                           @RequestParam Long toAccount,
                           @RequestParam double amount,
                           Model model,
                           HttpSession session) {

        if (notLoggedIn(session)) return "redirect:/";

        try {
            accountService.transfer(fromAccount, toAccount, amount);
            model.addAttribute("success", "Transfer successful!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "transfer";
    }

    // ================= LOANS =================

    @GetMapping("/personal-loan")
    public String personalLoanPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "personal-loan";
    }

    @GetMapping("/home-loan")
    public String homeLoanPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "home-loan";
    }

    // ================= CREDIT CARDS =================

    @GetMapping("/platinum-card")
    public String platinumCardPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "platinum-card";
    }

    @GetMapping("/travel-card")
    public String travelCardPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "travel-card";
    }

    // ================= ACCOUNT NUMBER GENERATOR =================

    private Long generateAccountNumber() {
        long min = 1000000000L;
        long max = 9999999999L;
        long number;

        do {
            number = min + (long)(Math.random() * (max - min));
        } while (accountRepository.existsById(number));

        return number;
    }
}