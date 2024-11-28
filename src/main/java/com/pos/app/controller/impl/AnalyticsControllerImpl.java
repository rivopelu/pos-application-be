package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.AnalyticsController;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.AnalyticsService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

@BaseControllerImpl
@RequiredArgsConstructor
public class AnalyticsControllerImpl implements AnalyticsController {
    private final AnalyticsService analyticsService;

    @Override
    public BaseResponse getAnalyticsSummary() {

        return ResponseHelper.createBaseResponse(analyticsService.getAnalyticsSummary());
    }
}
