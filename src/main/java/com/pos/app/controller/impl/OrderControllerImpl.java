package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.OrderController;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.OrderService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

@BaseControllerImpl
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {
    private final OrderService orderService;

    @Override
    public BaseResponse createOrder(ReqCreateOrder req) {
        return ResponseHelper.createBaseResponse(orderService.createOrder(req));
    }
}
