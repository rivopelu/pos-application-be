package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.SubscriptionController;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.SubscriptionService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@BaseControllerImpl
@RequiredArgsConstructor
public class SubscriptionControllerImpl implements SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Override
    public BaseResponse getSubscriptionsList(Pageable pageable) {
        return ResponseHelper.createBaseResponse(subscriptionService.getSubscriptionList(pageable));
    }
}
