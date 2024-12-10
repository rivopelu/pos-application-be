package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.AccountController;
import com.pos.app.model.request.ReqChangePassword;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.AccountService;
import com.pos.app.utils.ResponseHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;

@BaseControllerImpl
@AllArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    @Override
    public String newAccount(RequestCreateAccount req) {
        return accountService.createNewAccount(req);
    }

    @Override
    public BaseResponse newAccountByAdmin(RequestCreateAccount req) {
        return ResponseHelper.createBaseResponse(accountService.newAccountByAdmin(req));
    }

    @Override
    public BaseResponse getMe() {
        return ResponseHelper.createBaseResponse(accountService.getMeData());
    }

    @Override
    public BaseResponse listAccounts(Pageable pageable) {

        return  ResponseHelper.createBaseResponse(accountService.listAccount(pageable));
    }

    @Override
    public BaseResponse resetPassword(String id) {
        return ResponseHelper.createBaseResponse(accountService.resetPassword(id));
    }

    @Override
    public BaseResponse changePassword(ReqChangePassword req) {
        return ResponseHelper.createBaseResponse(accountService.changePassword(req));
    }

    @Override
    public BaseResponse inactiveAccount(String id) {

        return  ResponseHelper.createBaseResponse(accountService.inActiveAccount(id));
    }

    @Override
    public BaseResponse editProfile(RequestCreateAccount request) {

        return  ResponseHelper.createBaseResponse(accountService.editAccount(request));
    }
}
