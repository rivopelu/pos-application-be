package com.pos.app.repositories;

import com.pos.app.entities.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, String> {
    List<SubscriptionPackage> findAllByActiveIsTrue();
}
