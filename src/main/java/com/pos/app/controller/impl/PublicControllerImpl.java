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
import org.apache.coyote.Response;

@BaseControllerImpl
@RequiredArgsConstructor
public class PublicControllerImpl implements PublicController {

    private final MasterDataService masterDataService;
    private final OrderService orderService;

    @Override
    public BaseResponse getProductPublic(String code) {
        return ResponseHelper.createBaseResponse(masterDataService.getPublicAllProduct(code));
    }

    @Override
    public BaseResponse createOrderViaQr(String code, ReqCreateOrderViaQrCode req) {
        return ResponseHelper.createBaseResponse(orderService.createOrderViaQr(code, req));
    }

    @Override
    public BaseResponse getListOrderPublic(String code) {
        return ResponseHelper.createBaseResponse(orderService.getListOrderPublic(code));
    }

    @Override
    public BaseResponse requestBillOrder(String code) {
        return ResponseHelper.createBaseResponse(orderService.requestBill(code));
    }

    @Override
    public BaseResponse getListCategoryPublic(String code) {

        return ResponseHelper.createBaseResponse(masterDataService.getListCategoryPublic(code));
    }

    @Override
    public BaseResponse checkStatusOrder(String code) {
        return ResponseHelper.createBaseResponse(orderService.checkStatusOrder(code));
    }


}
