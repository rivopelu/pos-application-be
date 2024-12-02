package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.OrderController;
import com.pos.app.controller.PublicController;
import com.pos.app.model.request.ReqCreateOrderViaQrCode;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.MasterDataService;
import com.pos.app.service.OrderService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

@BaseControllerImpl
@RequiredArgsConstructor
public class PublicControllerImpl implements PublicController {

    private final MasterDataService masterDataService;
    private final OrderService orderService;
    private final OrderController order;

    @Override
    public BaseResponse getProductPublic(String code) {
        return ResponseHelper.createBaseResponse(masterDataService.getPublicAllProduct(code));
    }

    @Override
    public BaseResponse createOrderViaQr(String code, ReqCreateOrderViaQrCode req) {
        return ResponseHelper.createBaseResponse(orderService.createOrderViaQr(code, req));
    }


}
