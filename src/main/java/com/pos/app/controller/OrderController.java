package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@BaseController("order")
public interface OrderController {

    @PostMapping("v1/create-order")
    BaseResponse createOrder(@RequestBody ReqCreateOrder req);

    @GetMapping("v1/order-list")
    BaseResponse getOrderList(Pageable pageable);

    @PatchMapping("v1/order-ready/{id}")
    BaseResponse readyToTakeOrder(@PathVariable("id") String id);

    @PatchMapping("v1/order-complete/{id}")
    BaseResponse completeOrder(@PathVariable("id") String id);

    @GetMapping("v1/order-list/live")
    BaseResponse getLiveOrderList(@RequestParam(name = "status" ) OrderStatusEnum status);
}
