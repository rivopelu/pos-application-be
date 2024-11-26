package com.pos.app.service.impl;

import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public ResponseEnum createOrder(ReqCreateOrder req) {
        return ResponseEnum.SUCCESS;
    }
}
