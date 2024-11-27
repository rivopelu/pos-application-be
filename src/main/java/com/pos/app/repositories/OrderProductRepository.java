package com.pos.app.repositories;

import com.pos.app.entities.Order;
import com.pos.app.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, String> {
    List<OrderProduct> findAllByOrderId(String order_id);

}
