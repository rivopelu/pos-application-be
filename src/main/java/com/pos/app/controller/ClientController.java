package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateClient;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("client")
public interface ClientController {

    @PostMapping("v1/new")
    BaseResponse createClient(@RequestBody ReqCreateClient req);
}
