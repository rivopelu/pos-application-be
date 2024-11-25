package com.pos.app.repositories;

import com.pos.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByClientId(String currentClientIdOrNull);

}
