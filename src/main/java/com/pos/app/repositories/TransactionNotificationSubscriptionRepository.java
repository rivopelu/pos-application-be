package com.pos.app.repositories;

import com.pos.app.entities.TransactionNotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionNotificationSubscriptionRepository extends JpaRepository<TransactionNotificationSubscription, String> {
}
