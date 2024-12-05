package com.pos.app.repositories;

import com.pos.app.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);

    Boolean existsAccountByUsername(String username);

    Page<Account> findAllByClientId(String clientId, Pageable pageable);

    Optional<Account> findByEmail(String email);

    Boolean existsAccountByEmail(String email);
}
