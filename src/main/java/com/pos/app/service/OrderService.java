package com.pos.app.service;


import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.ResListOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    ResponseEnum createOrder(ReqCreateOrder req);

    Page<ResListOrder> getOrderList(Pageable pageable);
}
