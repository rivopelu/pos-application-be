package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.UserRole;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqSignUp;
import com.pos.app.model.request.RequestSignIn;
import com.pos.app.model.response.ResponseSignIn;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.repositories.ClientRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.AuthService;
import com.pos.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.EnumSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;

    @Override
    public ResponseSignIn signInStaff(RequestSignIn req) {
        Optional<Account> findAccount = accountRepository.findByUsername(req.getUsername());
        if (findAccount.isEmpty()) {
            throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
        }


        EnumSet<UserRole> allowedRoles = EnumSet.of(UserRole.STAFF, UserRole.ADMIN);
        if (!allowedRoles.contains(findAccount.get().getRole())) {
            throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
        }

        Client client = findAccount.get().getClient();
        if (client == null) {
            throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }

        if (!client.getIsActiveSubscription()) {
            throw new BadRequestException(ResponseEnum.SUBSCRIPTION_EXPIRED.name());
        }

        if (client.getSubscriptionExpiredDate() < new Date().getTime()) {
            throw new BadRequestException(ResponseEnum.SUBSCRIPTION_EXPIRED.name());
        }

        try {

            return getSignIn(findAccount.get(), req.getPassword());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseSignIn signInSuperAdmin(RequestSignIn req) {
        Optional<Account> findAccount = accountRepository.findByUsername(req.getUsername());
        if (findAccount.isEmpty()) {
            throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
        }
        if (findAccount.get().getRole() != UserRole.SUPER_ADMIN) {
            throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
        }
        try {
            return getSignIn(findAccount.get(), req.getPassword());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum signUp(ReqSignUp req) {

        Boolean checkEmail = accountRepository.existsAccountByEmail(req.getEmail());
        Boolean checkUsername = accountRepository.existsAccountByUsername(req.getUsername());

        if (checkEmail) {
            throw new BadRequestException(ResponseEnum.EMAIL_ALREADY_EXIST.name());
        }
        if (checkUsername) {
            throw new BadRequestException(ResponseEnum.USERNAME_ALREADY_EXIST.name());
        }


        String encode = passwordEncoder.encode(req.getPassword());
        Account account = Account.builder()
                .email(req.getEmail())
                .name(req.getName())
                .username(req.getUsername())
                .role(UserRole.ADMIN)
                .avatar(accountService.createAvatar(req.getName()))
                .password(encode)
                .createdBy("SYSTEM")
                .build();

        account = accountRepository.save(account);

        Client client = Client.builder()
                .name(req.getBusinessName())
                .address(req.getBusinessAddress())
                .createdBy(account.getId())
                .isActiveSubscription(false)
                .build();

        clientRepository.save(client);
        account.setClient(client);
        account.setCreatedBy(account.getId());


        try {
            accountRepository.save(account);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseSignIn signInAdmin(RequestSignIn req) {
        Optional<Account> findAccount = accountRepository.findByUsername(req.getUsername());
        if (findAccount.isEmpty()) {
            throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
        }
        Account account = findAccount.get();
        if (account.getRole() != UserRole.ADMIN) {
            throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
        }

        try {

            return getSignIn(account, req.getPassword());

        } catch (Exception e) {

            throw new SystemErrorException(e);

        }
    }

    private ResponseSignIn getSignIn(Account account, String password) {
        try {
            if (account.getIsInactive()) {
                throw new BadRequestException(ResponseEnum.SIGN_IN_FAILED.name());
            }
            Authentication authentication;
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getUsername(), password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);
            return ResponseSignIn.builder().accessToken(jwt).build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
