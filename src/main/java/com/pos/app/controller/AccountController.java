package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.RequestCreateAccount;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("auth")
public interface AccountController {

    @PostMapping("v1/new-account")
    String newAccount(@RequestBody RequestCreateAccount req);
}
