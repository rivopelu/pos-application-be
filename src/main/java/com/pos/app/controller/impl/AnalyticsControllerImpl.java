package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.AnalyticsController;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.AnalyticsService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Date;

@BaseControllerImpl
@RequiredArgsConstructor
public class AnalyticsControllerImpl implements AnalyticsController {
    private final AnalyticsService analyticsService;

    @Override
    public BaseResponse getAnalyticsSummary() {

        return ResponseHelper.createBaseResponse(analyticsService.getAnalyticsSummary());
    }

    @Override
    public BaseResponse getAnalyticsChartOrder(Date startDate, Date endDate) {

        return ResponseHelper.createBaseResponse(analyticsService.getAnalyticsChartOrder(startDate, endDate));
    }

    @Override
    public BaseResponse getAnalyticsChartRevenue(Date startDate, Date endDate) {
        return ResponseHelper.createBaseResponse(analyticsService.getAnalyticsChartRevenue(startDate, endDate));
    }

    @Override
    public BaseResponse getReportSales(Pageable pageable, Date startDate, Date endDate) {
        return ResponseHelper.createBaseResponse(analyticsService.getReportSales(pageable, startDate, endDate));
    }

    @Override
    public ResponseEntity<byte[]> downloadReport() {

        return analyticsService.downloadReport();
    }
}
