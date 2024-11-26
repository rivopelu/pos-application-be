package com.pos.app.repositories;

import com.pos.app.entities.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, String> {
}
