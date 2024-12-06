package com.pos.app.service;

import com.pos.app.model.request.ReqPaymentSubscription;
import com.pos.app.model.response.ResponsePaymentToken;

public interface SubscriptionService {

    ResponsePaymentToken paymentSubscription(ReqPaymentSubscription subscription);

}
