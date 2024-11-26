package com.pos.app.service;


import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateOrder;

public interface OrderService {

    ResponseEnum createOrder(ReqCreateOrder req);
}
