package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.PaymentController;
import com.pos.app.model.request.RequestTestingPayment;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.PaymentService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

@BaseControllerImpl
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {
    private final PaymentService paymentService;


    @Override
    public BaseResponse testingPayment(RequestTestingPayment req) {
        return ResponseHelper.createBaseResponse(paymentService.testingPayment(req));
    }

}
