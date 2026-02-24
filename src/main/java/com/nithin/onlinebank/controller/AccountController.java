package com.nithin.onlinebank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nithin.onlinebank.model.Account;
import com.nithin.onlinebank.service.BankService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private BankService accountService;

    
    @PostMapping("/create")
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

   
    @PutMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestParam double amount) {
        return accountService.deposit(id, amount);
    }

    
    @PutMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestParam double amount) {
        return accountService.withdraw(id, amount);
    }

    
    @PutMapping("/transfer")
    public String transfer(@RequestParam Long from,
                           @RequestParam Long to,
                           @RequestParam double amount) {

        accountService.transfer(from, to, amount);
        return "Transfer Successful";
    }
}