package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.response.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

@BaseController("subscription")
public interface SubscriptionController {
    @GetMapping("v1/list")
    BaseResponse getSubscriptionsList(Pageable pageable);
}
