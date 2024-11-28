package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.OrderController;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.OrderService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@BaseControllerImpl
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {
    private final OrderService orderService;

    @Override
    public BaseResponse createOrder(ReqCreateOrder req) {
        return ResponseHelper.createBaseResponse(orderService.createOrder(req));
    }

    @Override
    public BaseResponse getOrderList(Pageable pageable) {
        return ResponseHelper.createBaseResponse(orderService.getOrderList(pageable));
    }

    @Override
    public BaseResponse readyToTakeOrder(String id) {
        return ResponseHelper.createBaseResponse(orderService.readyToTakeOrder(id));
    }
}
