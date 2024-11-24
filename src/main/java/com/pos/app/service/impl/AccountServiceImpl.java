package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.UserRole;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.ResponseGetMe;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.service.AccountService;
import com.pos.app.utils.AuthConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;

    @Override
    public String createNewAccount(RequestCreateAccount req) {
        String passwordEncode = passwordEncoder.encode(req.getPassword());
        Account account = Account.builder()
                .username(req.getUsername())
                .name(req.getName())
                .password(passwordEncode)
                .role(req.getRole())
                .avatar(createAvatar(req.getName()))
                .createdBy(getCurrentAccountId() != null ? getCurrentAccountId() : "SYSTEM")
                .build();
        accountRepository.save(account);
        return "success";

    }

    @Override
    public String createAvatar(String name) {
        return "https://ui-avatars.com/api/?background=random&name=" + name;
    }

    @Override
    public Account getCurrentAccount() {
        String currentUserId = httpServletRequest.getAttribute(AuthConstant.HEADER_X_WHO).toString();
        Optional<Account> account = accountRepository.findById(currentUserId);
        return account.orElse(null);
    }

    @Override
    public Account getCurrentAccount(String id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ACCOUNT_NOT_FOUND.name());
        }
        return account.get();
    }

    @Override
    public String getCurrentAccountId() {
        return httpServletRequest.getAttribute(AuthConstant.HEADER_X_WHO) != null ? httpServletRequest.getAttribute(AuthConstant.HEADER_X_WHO).toString() : null;
    }

    @Override
    public ResponseGetMe getMeData() {
        try {
            Account account = getCurrentAccount();
            return ResponseGetMe.builder()
                    .name(account.getName())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
