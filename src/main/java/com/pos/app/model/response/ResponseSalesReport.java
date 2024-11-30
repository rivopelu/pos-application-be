package com.pos.app.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseSalesReport {

    private String productName;
    private String productId;
    private String orderId;
    private BigInteger qty;
    private BigInteger pricePerQty;
    private BigInteger totalPrice;
    private BigInteger totalTransaction;
    private BigInteger taxPercentage;
    private Long date;
}
