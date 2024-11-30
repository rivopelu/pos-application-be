package com.pos.app;

import com.pos.app.repositories.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
class PosApplicationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void contextLoads() {
        Assertions.assertThat(accountRepository).isNotNull();
    }

}
