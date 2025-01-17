package com.pos.app.service;


import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.request.ReqCreateOrderViaQrCode;
import com.pos.app.model.response.*;
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

    ResponseEnum createOrderViaQr(String code, ReqCreateOrderViaQrCode req);

    ResponseListOrderPublic getListOrderPublic(String code);

    ResponseEnum requestBill(String code);

    ResStatusOrder checkStatusOrder(String code);


    ResponseDetailOrder getDetailOrder(String id);
}
