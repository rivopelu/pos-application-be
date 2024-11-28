package com.pos.app.service.impl;

import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.response.ResponseAnalyticsSummary;
import com.pos.app.repositories.OrderProductRepository;
import com.pos.app.repositories.OrderRepository;
import com.pos.app.repositories.TransactionRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

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
}
