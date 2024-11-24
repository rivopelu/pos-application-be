package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.UserRole;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.ResponseGetMe;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.repositories.ClientRepository;
import com.pos.app.service.AccountService;
import com.pos.app.utils.AuthConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;
    private final ClientRepository clientRepository;

    @Override
    public String createNewAccount(RequestCreateAccount req) {
        Client client = null;
        String passwordEncode = passwordEncoder.encode(req.getPassword());
        EnumSet<UserRole> allowedRoles = EnumSet.of(UserRole.STAFF, UserRole.ADMIN);
        boolean checkUsername = accountRepository.existsAccountByUsername(req.getUsername());
        if (checkUsername) {
            throw new BadRequestException(ResponseEnum.ACCOUNT_ALREADY_EXIST.name());
        }
        if (allowedRoles.contains(req.getRole()) && req.getClientId() == null) {
            throw new BadRequestException(ResponseEnum.CLIENT_REQUIRED.name());
        }

        if (req.getClientId() != null) {
            Optional<Client> getClient = clientRepository.findById(req.getClientId());
            if (getClient.isEmpty()) {
                throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
            }
            client = getClient.get();

        }
        Account account = Account.builder()
                .username(req.getUsername())
                .name(req.getName())
                .password(passwordEncode)
                .role(req.getRole())
                .client(client)
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
    public String getCurrentClientId() {
        if (getCurrentAccount().getClient() == null) {
            throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }
        return getCurrentAccount().getClient().getId();
    }

    @Override
    public String getCurrentClientIdOrNull() {

        if (getCurrentAccount().getClient() == null) {
            return  null;
        }
        return getCurrentAccount().getClient().getId();
    }

    @Override
    public ResponseGetMe getMeData() {
        try {
            Account account = getCurrentAccount();
            ResponseGetMe responseGetMe = ResponseGetMe.builder()
                    .name(account.getName())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .build();


            if (account.getClient() != null) {
                Client client = account.getClient();
                responseGetMe.setClientId(client.getId());
                responseGetMe.setClientName(client.getName());
                responseGetMe.setClientLogo(client.getLogo());
            }
            return responseGetMe;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
