package com.nithin.onlinebank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nithin.onlinebank.model.Account;
import com.nithin.onlinebank.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class BankController {

    @Autowired
    private AccountRepository accountRepository;

    // ================= COMMON LOGIN CHECK METHOD =================
    private boolean notLoggedIn(HttpSession session) {
        return session.getAttribute("account") == null;
    }

    // ================= LOGIN PAGE =================
    @GetMapping("/")
    public String loginPage(HttpSession session) {
        return "login";
    }

    // ================= LOGIN PROCESS =================
    @PostMapping("/login")
    public String login(@RequestParam Long accountNo,
                        @RequestParam String pin,
                        Model model,
                        HttpSession session) {

        Account account = accountRepository.findByAccountNoAndPin(accountNo, pin);

        if (account == null) {
            model.addAttribute("error", "Invalid Account Number or PIN");
            return "login";
        }

        session.setAttribute("account", account);
        return "redirect:/dashboard";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "dashboard";
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

        if (!account.getPin().matches("\\d{4}")) {
            model.addAttribute("error", "PIN must be exactly 4 digits");
            model.addAttribute("account", account);
            return "create-account";
        }

        account.setAccountNo(generateAccountNumber());
        account.setKYCVerified(true);

        accountRepository.save(account);

        model.addAttribute("success",
                "Account Created Successfully! Your Account Number is: "
                        + account.getAccountNo()
                        + ". Please login to continue.");

        model.addAttribute("account", new Account());

        return "create-account";
    }

    // ================= VIEW BALANCE =================
    @GetMapping("/view-balance")
    public String viewBalancePage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "view-balance";
    }

    @PostMapping("/view-balance")
    public String checkBalance(@RequestParam String pin,
                               HttpSession session,
                               Model model) {

        if (notLoggedIn(session)) return "redirect:/";

        Account account = (Account) session.getAttribute("account");

        if (!account.getPin().equals(pin)) {
            model.addAttribute("error", "Incorrect PIN");
            return "view-balance";
        }

        model.addAttribute("balance", account.getBalance());
        return "view-balance";
    }

    // ================= DEPOSIT =================
    @GetMapping("/deposit")
    public String showDepositPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam double amount,
                          @RequestParam String pin,
                          HttpSession session,
                          Model model) {

        if (notLoggedIn(session)) return "redirect:/";

        Account account = (Account) session.getAttribute("account");

        if (!account.getPin().equals(pin)) {
            model.addAttribute("error", "Incorrect PIN");
            return "deposit";
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
        session.setAttribute("account", account);

        model.addAttribute("success", "Deposit Successful!");
        return "deposit";
    }

    // ================= WITHDRAW =================
    @GetMapping("/withdraw")
    public String showWithdrawPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam double amount,
                           @RequestParam String pin,
                           HttpSession session,
                           Model model) {

        if (notLoggedIn(session)) return "redirect:/";

        Account account = (Account) session.getAttribute("account");

        if (!account.getPin().equals(pin)) {
            model.addAttribute("error", "Incorrect PIN");
            return "withdraw";
        }

        if (account.getBalance() < amount) {
            model.addAttribute("error", "Insufficient Balance");
            return "withdraw";
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
        session.setAttribute("account", account);

        model.addAttribute("success", "Withdrawal Successful!");
        return "withdraw";
    }

    // ================= TRANSFER =================
    @GetMapping("/transfer")
    public String showTransferPage(HttpSession session) {
        if (notLoggedIn(session)) return "redirect:/";
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long toAccount,
                           @RequestParam double amount,
                           @RequestParam String pin,
                           HttpSession session,
                           Model model) {

        if (notLoggedIn(session)) return "redirect:/";

        Account fromAccount = (Account) session.getAttribute("account");

        if (!fromAccount.getPin().equals(pin)) {
            model.addAttribute("error", "Incorrect PIN");
            return "transfer";
        }

        Account receiver = accountRepository.findById(toAccount).orElse(null);

        if (receiver == null) {
            model.addAttribute("error", "Receiver not found");
            return "transfer";
        }

        if (fromAccount.getBalance() < amount) {
            model.addAttribute("error", "Insufficient Balance");
            return "transfer";
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(receiver);

        session.setAttribute("account", fromAccount);

        model.addAttribute("success", "Transfer Successful!");
        return "transfer";
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