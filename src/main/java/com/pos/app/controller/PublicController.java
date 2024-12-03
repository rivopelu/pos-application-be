package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateOrderViaQrCode;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("public")
public interface PublicController {

    @GetMapping("v1/product/list/{code}")
    BaseResponse getProductPublic(@PathVariable("code") String code);

    @PostMapping("v1/order/create/{code}")
    BaseResponse createOrderViaQr(@PathVariable String code, @RequestBody ReqCreateOrderViaQrCode req);

    @GetMapping("v1/order/list-order-public/{code}")
    BaseResponse getListOrderPublic(@PathVariable String code);

}
