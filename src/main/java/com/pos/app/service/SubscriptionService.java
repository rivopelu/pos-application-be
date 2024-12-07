package com.pos.app.service;

import com.pos.app.model.request.ReqPaymentSubscription;
import com.pos.app.model.response.ResponsePaymentToken;
import com.pos.app.model.response.ResponseSubscriptionList;
import com.pos.app.model.response.SnapPaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionService {

    SnapPaymentResponse paymentSubscription(ReqPaymentSubscription subscription);

    Page<ResponseSubscriptionList> getSubscriptionList(Pageable pageable);
}
