package com.pos.app.service;


import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.ResListOrder;
import com.pos.app.model.response.ResponseIdQr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    ResponseEnum createOrder(ReqCreateOrder req);

    Page<ResListOrder> getOrderList(Pageable pageable);

    ResponseEnum readyToTakeOrder(String id);

    ResponseEnum completeOrder(String id);

    List<ResListOrder> getLiveOrderList(OrderStatusEnum status);


    ResponseIdQr generateQROrder();
}
