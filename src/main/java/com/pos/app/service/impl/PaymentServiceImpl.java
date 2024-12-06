package com.pos.app.service.impl;

import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqPaymentObject;
import com.pos.app.model.request.RequestTestingPayment;
import com.pos.app.model.response.SnapPaymentResponse;
import com.pos.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${mt.server-key}")
    private String mtServerKey;

    @Value("${mt.api-url}")
    private String mtApiUrl;

    @Override
    public SnapPaymentResponse testingPayment(RequestTestingPayment req) {
        try {
            String url = mtApiUrl + "/snap/v1/transactions";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            String authString = mtServerKey + ":";
            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
            headers.set("Authorization", "Basic " + encodedAuthString);

            Map<String, Object> body = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            fields.put("order_id", UUID.randomUUID().toString());
            fields.put("gross_amount", req.getPrice());
            body.put("transaction_details", fields);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<SnapPaymentResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    SnapPaymentResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public SnapPaymentResponse createPayment(ReqPaymentObject req) {
        String url = mtApiUrl + "/snap/v1/transactions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String authString = mtServerKey + ":";
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        headers.set("Authorization", "Basic " + encodedAuthString);

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        fields.put("order_id", req.getTransactionDetail().getOrderId());
        fields.put("gross_amount", req.getTransactionDetail().getGrossAmount());
        body.put("transaction_details", fields);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SnapPaymentResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                SnapPaymentResponse.class
        );

        try {
            return response.getBody();
            
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
