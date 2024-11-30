package com.pos.app.service;

import com.pos.app.model.response.ResponseAnalyticsSummary;
import com.pos.app.model.response.ResponseChartOrder;

import java.util.Date;
import java.util.List;

public interface AnalyticsService {

    ResponseAnalyticsSummary getAnalyticsSummary();

    List<ResponseChartOrder> getAnalyticsChartOrder(Date startDate, Date endDate);

    List<ResponseChartOrder> getAnalyticsChartRevenue(Date startDate, Date endDate);
}
