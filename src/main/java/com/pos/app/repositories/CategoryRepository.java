package com.pos.app.repositories;

import com.pos.app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    boolean existsAllByClientId(String currentClientId);

    List<Category> findAllByClientIdOrderBySeqAsc(String clientId);

}
