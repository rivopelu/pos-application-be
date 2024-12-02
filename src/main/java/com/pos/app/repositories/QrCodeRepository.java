package com.pos.app.repositories;

import com.pos.app.entities.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrCodeRepository extends JpaRepository<QrCode, String> {
}
