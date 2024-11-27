package com.pos.app.repositories;

import com.pos.app.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("select o.orderCode from Order as o where o.clientId = :clientId order by  o.createdDate desc limit 1 ")
    BigInteger findLatestCode(String clientId);

}
