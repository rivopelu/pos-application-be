package com.pos.app.repositories;

import com.pos.app.entities.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
}
