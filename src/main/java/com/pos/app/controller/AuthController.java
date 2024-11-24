package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.RequestSignIn;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("auth")
public interface AuthController {

    @PostMapping("v1/sign-in/staff")
    BaseResponse signInStaff(@RequestBody RequestSignIn req);

    @PostMapping("v1/sign-in/super-admin")
    BaseResponse signInSuperAdmin(@RequestBody RequestSignIn req);

    @GetMapping("ping")
    String ping();

}
