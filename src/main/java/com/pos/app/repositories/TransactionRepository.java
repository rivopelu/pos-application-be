package com.pos.app.repositories;

import com.pos.app.entities.Order;
import com.pos.app.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByOrderId(String order_id);

    @Query(value = "select sum(t.totalTransaction) from Transaction  as t where t.clientId = :clientId")
    BigInteger sumTransactionByClientId(String clientId);

    Boolean existsAllByClientIdAndIsActiveTrue(String clientId);

    @Query(value = "SELECT DATE(FROM_UNIXTIME((o.created_date / 1000) + (:offset * 3600))) AS `date`, SUM(o.total_transaction) AS `total` " +
            "FROM transaction AS o " +
            "WHERE o.created_date BETWEEN UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 7 DAY)) * 1000 " +
            "AND UNIX_TIMESTAMP(CURDATE() + INTERVAL 1 DAY) * 1000 - 1 " +
            "AND o.client_id = :clientId " +
            "GROUP BY DATE(FROM_UNIXTIME((o.created_date / 1000) + (:offset * 3600))) " +
            "ORDER BY `date` ASC",
            nativeQuery = true)
    List<Object[]> getChartRevenue(@Param("offset") int offset, @Param("clientId") String clientId);

}
