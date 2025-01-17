package com.pos.app.service.impl;

import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.response.ResponseAnalyticsSummary;
import com.pos.app.model.response.ResponseChartOrder;
import com.pos.app.model.response.ResponseSalesReport;
import com.pos.app.repositories.OrderProductRepository;
import com.pos.app.repositories.OrderRepository;
import com.pos.app.repositories.TransactionRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final OrderProductRepository orderProductRepository;

    @Override
    public ResponseAnalyticsSummary getAnalyticsSummary() {
        String clientId = accountService.getCurrentClientIdOrNull();
        try {
            BigInteger getCountTotalOrder = orderRepository.countByClientId(clientId);
            BigInteger getTotalRevenue = transactionRepository.sumTransactionByClientId(clientId);
            BigInteger getTotalItems = orderProductRepository.getSumQty(clientId);
            return ResponseAnalyticsSummary.builder()
                    .totalOrder(getCountTotalOrder)
                    .totalRevenue(getTotalRevenue)
                    .totalItems(getTotalItems)
                    .build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public List<ResponseChartOrder> getAnalyticsChartOrder(Date startDate, Date endDate) {
        String clientId = accountService.getCurrentClientIdOrNull();
        Boolean checkValue = orderRepository.existsAllByClientIdAndIsActiveTrue(clientId);
        if (!checkValue) {
            return new ArrayList<>();
        }
        try {
            List<Object[]> list = orderRepository.getOrderChart(8, clientId);
            List<ResponseChartOrder> responseCharts = new ArrayList<>();
            if (!list.isEmpty()) {
                for (Object[] obj : list) {
                    ResponseChartOrder.ResponseChartOrderBuilder responseOrderChartBuilder = ResponseChartOrder.builder()
                            .label((Date) obj[0])
                            .value(obj[1]);
                    responseCharts.add(responseOrderChartBuilder.build());
                }
            }
            return responseCharts;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public List<ResponseChartOrder> getAnalyticsChartRevenue(Date startDate, Date endDate) {
        String clientId = accountService.getCurrentClientIdOrNull();
        Boolean checkValue = transactionRepository.existsAllByClientIdAndIsActiveTrue(clientId);

        if (!checkValue) {
            return new ArrayList<>();
        }
        try {
            List<Object[]> list = transactionRepository.getChartRevenue(8, clientId);
            List<ResponseChartOrder> responseCharts = new ArrayList<>();
            if (!list.isEmpty()) {
                for (Object[] obj : list) {
                    ResponseChartOrder.ResponseChartOrderBuilder responseOrderChartBuilder = ResponseChartOrder.builder()
                            .label((Date) obj[0])
                            .value(obj[1]);
                    responseCharts.add(responseOrderChartBuilder.build());
                }
            }
            return responseCharts;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }

    }

    @Override
    public Page<ResponseSalesReport> getReportSales(Pageable pageable, Date startDate, Date endDate) {
        String clientId = accountService.getCurrentClientIdOrNull();
        List<ResponseSalesReport> responseSalesReportList = new ArrayList<>();
        Page<Object[]> orderProductPage = orderProductRepository.getSalesReport(pageable, clientId);
        try {
            for (Object[] obj : orderProductPage.getContent()) {
                ResponseSalesReport responseSalesReport = ResponseSalesReport.builder()
                        .productId((String) obj[0])
                        .productName((String) obj[1])
                        .orderId((String) obj[2])
                        .qty((BigInteger) obj[3])
                        .pricePerQty((BigInteger) obj[4])
                        .totalPrice((BigInteger) obj[5])
                        .totalTransaction((BigInteger) obj[6])
                        .taxPercentage((BigInteger) obj[7])
                        .date((Long) obj[8])
                        .build();
                responseSalesReportList.add(responseSalesReport);
            }
            return new PageImpl<>(responseSalesReportList, pageable, orderProductPage.getTotalElements());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadReport() {
        String clientId = accountService.getCurrentClientIdOrNull();
        List<Object[]> orderProductPage = orderProductRepository.getSalesReport(clientId);
        try {
            String[] headers = {"Product ID", "Product name", "order ID", "qty", "price per qty", "total price", "total transaction", "tax", "date"};
            String[][] data = new String[orderProductPage.size()][headers.length];

            int index = 0;
            for (Object[] obj : orderProductPage) {
                String[] objData = new String[obj.length];
                for (int i = 0; i < obj.length; i++) {

                    if (obj[i] instanceof Long) { // Check if the object is a Long (Unix time)
                        long unixTime = (Long) obj[i];
                        Date date = new Date(unixTime);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String formattedDate = formatter.format(date);

                        objData[i] = formattedDate;
                    } else {
                        objData[i] = obj[i] != null ? obj[i].toString() : "";
                    }

                }
                data[index] = objData;
                index++;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream));

            writer.println(String.join(",", headers));

            for (String[] row : data) {
                writer.println(String.join(",", row));
            }
            writer.flush();
            writer.close();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Disposition", "attachment; filename=data.csv");

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
