package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.AuthController;
import com.pos.app.entities.Account;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.UserRole;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.RequestSignIn;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.model.response.ResponseSignIn;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.AuthService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@BaseControllerImpl
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;


    @Override
    public BaseResponse signInStaff(RequestSignIn req) {
        return ResponseHelper.createBaseResponse(authService.signInStaff(req));
    }

    @Override
    public BaseResponse signInSuperAdmin(RequestSignIn req) {
        return ResponseHelper.createBaseResponse(authService.signInSuperAdmin(req));
    }
}
