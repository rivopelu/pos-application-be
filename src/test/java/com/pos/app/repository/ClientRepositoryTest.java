package com.pos.app.repository;


import com.github.javafaker.Faker;
import com.pos.app.entities.Client;
import com.pos.app.repositories.ClientRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class ClientRepositoryTest {
    Faker faker = new Faker();
    String name = faker.name().fullName();

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void contextLoads() {
        Assertions.assertThat(clientRepository).isNotNull();
    }

    @Test
    public void testSave() {
        Client client = saveClient();
        Assertions.assertThat(client).isNotNull();
        Assertions.assertThat(client.getId()).isNotNull();
        Assertions.assertThat(client.getName()).isEqualTo(name);
    }



    private Client saveClient() {
        Client client = Client.builder()
                .name(name)
                .createdBy(name)
                .build();
        return clientRepository.save(client);
    }

}
