package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.UserRole;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.RequestSignIn;
import com.pos.app.model.response.ResponseSignIn;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.AuthService;
import com.pos.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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
