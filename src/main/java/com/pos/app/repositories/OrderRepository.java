package com.pos.app.repositories;

import com.pos.app.entities.Order;
import com.pos.app.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("select o.orderCode from Order as o where o.clientId = :clientId order by  o.createdDate desc limit 1 ")
    BigInteger findLatestCode(String clientId);

    Page<Order> findByClientIdAndIsActiveTrueOrderByCreatedDateDesc(Pageable pageable, String clientId);

    List<Order> findAllByClientIdAndStatusOrderByUpdatedDateAsc(String clientId, OrderStatusEnum status);

    BigInteger countByClientId(String clientId);

    Boolean existsAllByClientIdAndIsActiveTrue(String clientId);

    @Query(value = "SELECT DATE(FROM_UNIXTIME((o.created_date / 1000) + (:offset * 3600))) AS `date`, COUNT(o.id) AS `count` " +
            "FROM `order` AS o " +
            "WHERE o.created_date BETWEEN UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 7 DAY)) * 1000 " +
            "AND UNIX_TIMESTAMP(CURDATE() + INTERVAL 1 DAY) * 1000 - 1 " +
            "AND o.client_id  = :clientId" +
            "GROUP BY DATE(FROM_UNIXTIME((o.created_date / 1000) + (:offset * 3600))) " +
            "ORDER BY `date` ASC",
            nativeQuery = true)
    List<Object[]> getOrderChart(@Param("offset") int offset, @Param("clientId") String clientId);

}
