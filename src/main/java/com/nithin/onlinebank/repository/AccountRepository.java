package com.nithin.onlinebank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nithin.onlinebank.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountNoAndPin(Long accountNo, String pin);
}