package com.pos.app.repository;

import com.github.javafaker.Faker;
import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.enums.UserRole;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.repositories.ClientRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class AccountRepositoryTest {
    Faker faker = new Faker();
    String name = faker.name().fullName();
    String username = faker.name().username();
    String password = faker.name().username();


    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void contextLoads() {
        Assertions.assertThat(accountRepository).isNotNull();
    }

    @Test
    public void testSaveAccount() {

        Account account = Account.builder()
                .name(name)
                .username(username)
                .createdBy(faker.name().username())
                .build();
        Account savedAccount = accountRepository.save(account);

        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getId()).isNotNull();
        Assertions.assertThat(savedAccount.getName()).isEqualTo(name);
        Assertions.assertThat(savedAccount.getCreatedBy()).isNotNull();
        Assertions.assertThat(savedAccount.getUsername()).isEqualTo(username);
        Assertions.assertThat(savedAccount.getCreatedBy()).isNotNull();
        Assertions.assertThat(savedAccount.getCreatedDate()).isNotNull();

    }

    @Test
    public void testExistUsername() {
        Account account = Account.builder()
                .name(name)
                .username(username)
                .password(password)
                .role(UserRole.ADMIN)
                .createdBy(faker.name().username())  // Proper use of faker
                .build();
        accountRepository.save(account);

        boolean checkExist = accountRepository.existsAccountByUsername(username);

        Assertions.assertThat(checkExist).isTrue();
    }

    @Test
    public void testFindByClientId() {
        Client client = Client.builder()
                .createdBy(faker.name().username())
                .name(faker.company().name())
                .build();
        Client saveClient = clientRepository.save(client);
        Pageable pageable = PageRequest.of(0, 10);  // Page 0 with 10 items per page

        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Account account = Account.builder()
                    .name(name + i)
                    .username(username + i)
                    .password(password)
                    .role(UserRole.ADMIN)
                    .client(saveClient)
                    .createdBy(faker.name().username())
                    .build();
            accountList.add(account);
        }
        accountRepository.saveAll(accountList);
        Page<Account> getListAccount = accountRepository.findAllByClientId(client.getId(), pageable);
        Assertions.assertThat(getListAccount.getTotalElements()).isEqualTo(3);


    }
}