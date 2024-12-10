package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqChangePassword;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@BaseController("account")
public interface AccountController {

    @PostMapping("v1/new-account")
    String newAccount(@RequestBody RequestCreateAccount req);

    @PostMapping("v1/admin/new-account")
    BaseResponse newAccountByAdmin(@RequestBody RequestCreateAccount req);

    @GetMapping("v1/get-me")
    BaseResponse getMe();

    @GetMapping("v1/list")
    BaseResponse listAccounts(Pageable pageable);

    @PatchMapping("v1/reset-password/{id}")
    BaseResponse resetPassword(@PathVariable String id);

    @PutMapping("v1/change-password")
    BaseResponse changePassword(@RequestBody ReqChangePassword req);

    @PatchMapping("v1/inactive-account/{id}")
    BaseResponse inactiveAccount(@PathVariable String id);

    @PutMapping("v1/edit/profile")
    BaseResponse editProfile(@RequestBody RequestCreateAccount request);

}
