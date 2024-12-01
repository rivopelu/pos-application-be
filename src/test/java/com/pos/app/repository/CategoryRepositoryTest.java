package com.pos.app.repository;

import com.github.javafaker.Faker;
import com.pos.app.entities.Category;
import com.pos.app.entities.Client;
import com.pos.app.repositories.CategoryRepository;
import com.pos.app.repositories.ClientRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryTest {
    Faker faker = new Faker();
    String name = faker.name().fullName();


    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ClientRepository clientRepository;


    @Test
    public void contextLoads() {
        Assertions.assertThat(categoryRepository).isNotNull();
    }

    @Test
    public void TestSaveCategory() {
        Client client = saveCategory();
        Assertions.assertThat(categoryRepository.count()).isEqualTo(6);
        Assertions.assertThat(categoryRepository.findAll()).hasSize(6);
        Assertions.assertThat(categoryRepository.findAllByClientIdOrderBySeqAsc(client.getId())).hasSize(3);

    }

    @Test
    public void testExistClientIdTrue() {
        Client client = saveCategory();
        boolean checkExistClientId = categoryRepository.existsAllByClientId(client.getId());
        Assertions.assertThat(checkExistClientId).isTrue();
    }

    @Test
    public void testExistClientIdFalse() {
        boolean checkExistClientId = categoryRepository.existsAllByClientId(UUID.randomUUID().toString());
        Assertions.assertThat(checkExistClientId).isFalse();
    }

    private Client saveCategory() {
        List<Category> categories = new ArrayList<>();
        Client client = Client.builder()
                .createdBy(faker.name().username())
                .name(faker.company().name())
                .build();
        Client saveClient = clientRepository.save(client);
        for (int i = 0; i < 6; i++) {
            Category category = Category.builder()
                    .name(name)
                    .seq(i)
                    .createdBy(faker.name().username())
                    .clientId(i % 2 == 0 ? UUID.randomUUID().toString() : saveClient.getId())
                    .build();
            categories.add(category);
        }
        categoryRepository.saveAll(categories);
        return saveClient;
    }
}
