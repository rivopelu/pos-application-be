package com.pos.app.service.impl;

import com.pos.app.constants.UrlSting;
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

import java.math.BigInteger;
import java.util.*;

import static com.pos.app.constants.UrlSting.GET_PAYMENT_SNAP_MID_TRANS_URL;

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
        String url = mtApiUrl + GET_PAYMENT_SNAP_MID_TRANS_URL;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String authString = mtServerKey + ":";
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        headers.set("Authorization", "Basic " + encodedAuthString);

        Map<String, Object> body = new HashMap<>();

        body.put("transaction_details", generateTransactionDetail(req.getTransactionDetail()));
        body.put("item_detail", generateItemsDetail(req.getItemsDetail()));
        body.put("customer_details", generateCustomersDetail(req.getCustomersDetails()));

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


    private Map<String, Object> generateTransactionDetail(ReqPaymentObject.TransactionDetail detail) {
        Map<String, Object> data = new HashMap<>();
        data.put("order_id", detail.getOrderId());
        data.put("gross_amount", detail.getGrossAmount());
        return data;
    }


    private List<Map<String, Object>> generateItemsDetail(ReqPaymentObject.ItemsDetail detail) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", detail.getId());
        data.put("price", detail.getPrice());
        data.put("name", detail.getName());

        List<Map<String, Object>> result = new ArrayList<>();
        result.add(data);

        return result;
    }


    private Map<String, Object> generateCustomersDetail(ReqPaymentObject.CustomersDetails detail) {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("first_name", detail.getFirstName());
        customerDetails.put("email", detail.getEmail());
        return customerDetails;
    }
}


