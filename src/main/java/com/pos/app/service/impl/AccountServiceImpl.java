package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public String createNewAccount(RequestCreateAccount req) {
        Account account = Account.builder()
                .username(req.getUsername())
                .name(req.getName())
                .password(req.getPassword())
                .build();
        accountRepository.save(account);
        return "success";

    }
}
