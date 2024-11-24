package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.AccountController;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.AccountService;
import com.pos.app.utils.ResponseHelper;
import lombok.AllArgsConstructor;

@BaseControllerImpl
@AllArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    @Override
    public String newAccount(RequestCreateAccount req) {
        return accountService.createNewAccount(req);
    }

    @Override
    public BaseResponse getMe() {
        return ResponseHelper.createBaseResponse(accountService.getMeData());
    }
}
