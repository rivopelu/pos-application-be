package com.pos.app.model.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pos.app.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReqCreateOrder {

    private String orderId;
    private OrderStatusEnum status;
    private String customerName;
    private Integer tax;
    private List<ListProductCreateOrder> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonSerialize
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ListProductCreateOrder {
        private String productId;
        private Integer qty;
    }


}
