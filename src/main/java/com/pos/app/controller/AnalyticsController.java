package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@BaseController("analytics")
public interface AnalyticsController {

    @GetMapping("v1/summary")
    BaseResponse getAnalyticsSummary();

    @GetMapping("v1/chart-order")
    BaseResponse getAnalyticsChartOrder(
            @RequestParam("start-date") Date startDate,
            @RequestParam("end-date") Date endDate
    );
}
