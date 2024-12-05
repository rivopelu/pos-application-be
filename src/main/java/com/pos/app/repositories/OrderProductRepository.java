package com.pos.app.repositories;

import com.pos.app.entities.Order;
import com.pos.app.entities.OrderProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, String> {
    List<OrderProduct> findAllByOrderId(String order_id);

    @Query(value = "select sum(op.qty) from OrderProduct  as op where op.clientId = :clientId")
    BigInteger getSumQty(String clientId);


    @Query(value = "select p.id, p.name," +
            " o.id, op.qty, op.pricePerQty," +
            " op.totalPrice, t.totalTransaction, " +
            "t.taxPercentage, op.createdDate" +
            " from OrderProduct as op " +
            "join Product  as p on op.product.id = p.id " +
            "join Order as o on o.id = op.order.id " +
            "join Transaction  as t on t.order.id = o.id " +
            "where op.clientId = :clientId " +
            "order by op.createdDate desc ")
    Page<Object[]> getSalesReport(Pageable pageable, String clientId);

    @Query(value = "select p.id, p.name," +
            " o.id, op.qty, op.pricePerQty," +
            " op.totalPrice, t.totalTransaction, " +
            "t.taxPercentage, op.createdDate " +
            "from OrderProduct as op " +
            "join Product  as p on op.product.id = p.id " +
            "join Order as o on o.id = op.order.id " +
            "join Transaction  as t on t.order.id = o.id " +
            "where op.clientId = :clientId " +
            "order by op.createdDate desc ")
    List<Object[]> getSalesReport(String clientId);
}
