package com.pos.app.service;

import com.pos.app.model.response.ResponseAnalyticsSummary;
import com.pos.app.model.response.ResponseChartOrder;
import com.pos.app.model.response.ResponseSalesReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface AnalyticsService {

    ResponseAnalyticsSummary getAnalyticsSummary();

    List<ResponseChartOrder> getAnalyticsChartOrder(Date startDate, Date endDate);

    List<ResponseChartOrder> getAnalyticsChartRevenue(Date startDate, Date endDate);

    Page getReportSales(Pageable pageable, Date startDate, Date endDate);
}
