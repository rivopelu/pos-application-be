package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.RequestTestingPayment;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("payment")
public interface PaymentController {

    @PostMapping("v1/test")
    BaseResponse testingPayment(@RequestBody RequestTestingPayment req);

}