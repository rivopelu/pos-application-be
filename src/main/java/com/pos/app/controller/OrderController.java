package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("order")
public interface OrderController {

    @PostMapping("v1/create-order")
    BaseResponse createOrder(@RequestBody ReqCreateOrder req);

    @GetMapping("v1/order-list")
    BaseResponse getOrderList(Pageable pageable);
}
