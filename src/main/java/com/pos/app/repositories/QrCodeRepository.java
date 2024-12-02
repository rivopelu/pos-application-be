package com.pos.app.repositories;

import com.pos.app.entities.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrCodeRepository extends JpaRepository<QrCode, String> {
    Optional<QrCode> findByCode(String code);
}
