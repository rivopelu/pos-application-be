package com.pos.app.service;

import com.pos.app.model.request.ReqPaymentSubscription;
import com.pos.app.model.response.ResponsePaymentToken;
import com.pos.app.model.response.SnapPaymentResponse;

public interface SubscriptionService {

    SnapPaymentResponse paymentSubscription(ReqPaymentSubscription subscription);

}
