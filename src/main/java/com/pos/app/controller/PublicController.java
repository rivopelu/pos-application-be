package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateOrderViaQrCode;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.*;

@BaseController("public")
public interface PublicController {

    @GetMapping("v1/product/list/{code}")
    BaseResponse getProductPublic(@PathVariable("code") String code);

    @PostMapping("v1/order/create/{code}")
    BaseResponse createOrderViaQr(@PathVariable String code, @RequestBody ReqCreateOrderViaQrCode req);

    @GetMapping("v1/order/list-order-public/{code}")
    BaseResponse getListOrderPublic(@PathVariable String code);

    @PatchMapping("v1/request-bill/{code}")
    BaseResponse requestBillOrder(@PathVariable String code);

    @GetMapping("v1/category-list/{code}")
    BaseResponse getListCategoryPublic(@PathVariable String code);
}
