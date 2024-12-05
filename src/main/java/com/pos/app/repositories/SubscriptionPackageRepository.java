package com.pos.app.repositories;

import com.pos.app.entities.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, Integer> {
}
