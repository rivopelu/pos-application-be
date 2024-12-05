package com.pos.app.service;

import com.pos.app.model.request.RequestTestingPayment;
import com.pos.app.model.response.SnapPaymentResponse;

public interface PaymentService {
    SnapPaymentResponse testingPayment(RequestTestingPayment req);
}
