package com.pos.app.service.impl;

import com.pos.app.constants.UrlSting;
import com.pos.app.entities.SubscriptionOrder;
import com.pos.app.entities.TransactionNotificationSubscription;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqNotificationMidTrans;
import com.pos.app.model.request.ReqPaymentObject;
import com.pos.app.model.request.RequestTestingPayment;
import com.pos.app.model.response.SnapPaymentResponse;
import com.pos.app.repositories.SubscriptionOrderRepository;
import com.pos.app.repositories.TransactionNotificationSubscriptionRepository;
import com.pos.app.service.PaymentService;
import com.pos.app.utils.EntityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.pos.app.constants.UrlSting.GET_PAYMENT_SNAP_MID_TRANS_URL;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final TransactionNotificationSubscriptionRepository transactionNotificationSubscriptionRepository;

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
            ResponseEntity<SnapPaymentResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, SnapPaymentResponse.class);

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
        ResponseEntity<SnapPaymentResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, SnapPaymentResponse.class);

        try {
            return response.getBody();

        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ReqNotificationMidTrans postNotificationFromMidTrans(ReqNotificationMidTrans req) {
        Optional<SubscriptionOrder> findOrder = subscriptionOrderRepository.findById(req.getOrderId());
        SubscriptionOrder subscriptionOrder = null;
        if (findOrder.isPresent()) {
            subscriptionOrder = findOrder.get();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(req.getTransactionTime(), formatter);
        long unixTime = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        TransactionNotificationSubscription detail = TransactionNotificationSubscription.builder()
                .transactionTime(unixTime)
                .transactionStatus(req.getTransactionStatus())
                .transactionId(req.getTransactionId())
                .statusMessage(req.getStatusMessage())
                .statusCode(req.getStatusCode())
                .signatureKey(req.getSignatureKey())
                .paymentType(req.getPaymentType())
                .orderId(req.getOrderId())
                .merchantId(req.getMerchantId())
                .maskedCard(req.getMaskedCard())
                .grossAmount(req.getGrossAmount())
                .fraudStatus(req.getFraudStatus())
                .eci(req.getEci())
                .currency(req.getCurrency())
                .channelResponseMessage(req.getChannelResponseMessage())
                .channelResponseCode(req.getChannelResponseCode())
                .cardType(req.getCardType())
                .bank(req.getBank())
                .approvalCode(req.getApprovalCode())
                .subscriptionOrder(subscriptionOrder)
                .build();

        EntityUtils.created(detail, "MID_TRANS");

        try {
            transactionNotificationSubscriptionRepository.save(detail);
            return req;
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


