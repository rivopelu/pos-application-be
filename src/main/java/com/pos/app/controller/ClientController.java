package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateClient;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("client")
public interface ClientController {

    @PostMapping("v1/new")
    BaseResponse createClient(@RequestBody ReqCreateClient req);

    @PostMapping("v1/create-by-user")
    BaseResponse createClientByUser(@RequestBody ReqCreateClient req);

    @PutMapping("v1/edit")
    BaseResponse editClient(@RequestBody ReqCreateClient req);

}
