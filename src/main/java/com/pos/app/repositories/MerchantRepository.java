package com.pos.app.repositories;

import com.pos.app.entities.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    List<Merchant> findAllByClientIdAndActiveIsTrue(String clientId);
}
