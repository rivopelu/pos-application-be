package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.entities.Merchant;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.UserRole;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqChangePassword;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.ResponseGetMe;
import com.pos.app.model.response.ResponseListAccount;
import com.pos.app.model.response.ResponsePasswordCreateAccount;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.repositories.ClientRepository;
import com.pos.app.repositories.MerchantRepository;
import com.pos.app.service.AccountService;
import com.pos.app.utils.AuthConstant;
import com.pos.app.utils.UtilsHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;
    private final ClientRepository clientRepository;
    private final AuthenticationManager authenticationManager;
    private final MerchantRepository merchantRepository;

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
            return null;
        }
        return getCurrentAccount().getClient().getId();
    }

    @Override
    public ResponseGetMe getMeData() {
        try {
            Account account = getCurrentAccount();
            ResponseGetMe responseGetMe = ResponseGetMe.builder()
                    .name(account.getName())
                    .email(account.getEmail())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .build();

            if (account.getMerchant() != null) {
                Merchant merchant = account.getMerchant();
                responseGetMe.setMerchantId(merchant.getId());
                responseGetMe.setMerchantName(merchant.getName());
            }


            if (account.getClient() != null) {
                Client client = account.getClient();
                responseGetMe.setClientId(client.getId());
                responseGetMe.setClientName(client.getName());
                responseGetMe.setClientLogo(client.getLogo());
                responseGetMe.setAddress(client.getAddress());
                responseGetMe.setIsActiveSubscription(client.getIsActiveSubscription());
                responseGetMe.setSubscriptionExpiredDate(client.getSubscriptionExpiredDate());
            }
            return responseGetMe;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponsePasswordCreateAccount newAccountByAdmin(RequestCreateAccount req) {
        String passwordGenerated = UtilsHelper.generateRandomPassword();
        String passwordEncode = passwordEncoder.encode(passwordGenerated);
        boolean checkUsername = accountRepository.existsAccountByUsername(req.getUsername());
        if (checkUsername) {
            throw new BadRequestException(ResponseEnum.ACCOUNT_ALREADY_EXIST.name());
        }


        Optional<Client> getClient = clientRepository.findById(getCurrentClientId());
        if (getClient.isEmpty()) {
            throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }
        Client client = getClient.get();
        try {

            Account account = Account.builder()
                    .username(req.getUsername())
                    .name(req.getName())
                    .password(passwordEncode)
                    .role(req.getRole())
                    .client(client)
                    .avatar(createAvatar(req.getName()))
                    .createdBy(getCurrentAccountId())
                    .build();
            accountRepository.save(account);
            return ResponsePasswordCreateAccount.builder()
                    .password(passwordGenerated)
                    .build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }

    }

    @Override
    public Page<ResponseListAccount> listAccount(Pageable pageable) {
        String clientId = getCurrentClientIdOrNull();
        List<ResponseListAccount> responseListAccounts = new ArrayList<>();
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
        }

        Page<Account> accountPage = accountRepository.findAllByClientId(clientId, pageable);
        for (Account account : accountPage.getContent()) {
            ResponseListAccount response = ResponseListAccount.builder()
                    .name(account.getName())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .id(account.getId())
                    .role(account.getRole())
                    .isInactive(account.getIsInactive())
                    .createdDate(account.getCreatedDate())
                    .createdBy(getCurrentAccount(account.getCreatedBy()).getName())
                    .build();
            responseListAccounts.add(response);
        }
        try {
            return new PageImpl<>(responseListAccounts, pageable, accountPage.getTotalElements());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponsePasswordCreateAccount resetPassword(String id) {
        Optional<Account> findAccount = accountRepository.findById(id);
        String passwordGenerated = UtilsHelper.generateRandomPassword();
        if (findAccount.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ACCOUNT_NOT_FOUND.name());
        }
        Account account = findAccount.get();
        String passwordEncode = passwordEncoder.encode(passwordGenerated);
        account.setPassword(passwordEncode);
        accountRepository.save(account);
        try {
            return ResponsePasswordCreateAccount.builder()
                    .password(passwordGenerated)
                    .build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum changePassword(ReqChangePassword req) {
        Account account = getCurrentAccount();
        try {
            Authentication authentication;
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getUsername(), req.getOldPassword()));
            if (!authentication.isAuthenticated()) {
                throw new BadRequestException(ResponseEnum.WRONG_OLD_PASSWORD.name());
            }
            String encodePassword = passwordEncoder.encode(req.getNewPassword());
            account.setPassword(encodePassword);
            accountRepository.save(account);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum inActiveAccount(String id) {


        try {

            Optional<Account> findAccount = accountRepository.findById(id);
            if (findAccount.isEmpty()) {
                throw new NotFoundException(ResponseEnum.ACCOUNT_NOT_FOUND.name());
            }
            Account account = findAccount.get();

            account.setIsInactive(true);
            accountRepository.save(account);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }

    }

    @Override
    public ResponseEnum editAccount(RequestCreateAccount request) {
        Account currentAccount = getCurrentAccount();

        Merchant merchant = null;
        if (request.getMerchantId() != null) {
            merchant = merchantRepository.findByIdAndActiveIsTrue(request.getMerchantId())
                    .orElseThrow(() -> new NotFoundException(ResponseEnum.MERCHANT_NOT_FOUND.name()));
        }

        if (request.getName() != null) {
            currentAccount.setName(request.getName());
        }
        if (request.getEmail() != null) {
            currentAccount.setEmail(request.getEmail());
        }
        if (merchant != null) {
            currentAccount.setMerchant(merchant);
        }
        if (request.getAvatar() != null) {
            currentAccount.setAvatar(request.getAvatar());
        }
        if (request.getUsername() != null) {
            currentAccount.setUsername(request.getUsername());
        }
        try {
            currentAccount.setUpdatedDate(new Date().getTime());

            accountRepository.save(currentAccount);
            return ResponseEnum.SUCCESS;

        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
