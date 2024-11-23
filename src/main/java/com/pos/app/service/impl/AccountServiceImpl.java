package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.enums.UserRole;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public String createNewAccount(RequestCreateAccount req) {
        String passwordEncode = passwordEncoder.encode(req.getPassword());
        Account account = Account.builder()
                .username(req.getUsername())
                .name(req.getName())
                .password(passwordEncode)
                .role(UserRole.SUPER_ADMIN)
                .createdBy("SYSTEM")
                .build();
        accountRepository.save(account);
        return "success";

    }
}
