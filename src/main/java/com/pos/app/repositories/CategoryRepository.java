package com.pos.app.repositories;

import com.pos.app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

    boolean existsAllByClientId(String currentClientId);

}
