package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.RequestCreateAccount;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("account")
public interface AccountController {

    @PostMapping("v1/new-account")
    String newAccount(@RequestBody RequestCreateAccount req);

    @GetMapping("v1/get-me")
    BaseResponse getMe();
}
