package com.nithin.onlinebank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nithin.onlinebank.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}