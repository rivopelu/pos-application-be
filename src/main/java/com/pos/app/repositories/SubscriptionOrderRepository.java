package com.pos.app.repositories;

import com.pos.app.entities.SubscriptionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionOrderRepository extends JpaRepository<SubscriptionOrder, String> {
    Page<SubscriptionOrder> findAllByClientIdOrderByCreatedDateDesc(Pageable pageable, String clientId);
}
