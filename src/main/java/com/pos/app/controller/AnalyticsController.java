package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;

@BaseController("analytics")
public interface AnalyticsController {

    @GetMapping("v1/summary")
    BaseResponse getAnalyticsSummary();
}
