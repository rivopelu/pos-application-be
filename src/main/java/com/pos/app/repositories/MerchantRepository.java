package com.pos.app.repositories;

import com.pos.app.entities.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    List<Merchant> findAllByClientIdAndActiveIsTrue(String clientId);

    Optional<Merchant> findByIdAndActiveIsTrue(String id);
}
