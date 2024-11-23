package com.pos.app.repositories;

import com.pos.app.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);
}
