package com.pos.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.RequestTestingPayment;
import com.pos.app.model.response.SnapPaymentResponse;
import com.pos.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
            URL url = new URL(mtApiUrl + "/snap/v1/transactions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String authString = mtServerKey + ":";
            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthString);

            Map<String, Object> body = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            fields.put("order_id", UUID.randomUUID().toString());
            fields.put("gross_amount", req.getPrice());
            body.put("transaction_details", fields);
            String requestBody = new ObjectMapper().writeValueAsString(body);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300
                            ? connection.getInputStream()
                            : connection.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.toString(), SnapPaymentResponse.class);

        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}
