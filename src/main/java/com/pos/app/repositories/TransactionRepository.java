package com.pos.app.repositories;

import com.pos.app.entities.Order;
import com.pos.app.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("select t from Transaction as t where t.order.id in :orderIds")
    List<Transaction> findAllByOrderId(List<String> orderIds);
}
