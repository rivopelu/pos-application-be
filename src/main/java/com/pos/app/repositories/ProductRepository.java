package com.pos.app.repositories;

import com.pos.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByClientId(String currentClientIdOrNull);

    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findExistingIdsByIds(@Param("ids") List<String> ids);

}
