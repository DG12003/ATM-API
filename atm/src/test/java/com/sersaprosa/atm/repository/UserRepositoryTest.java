package com.sersaprosa.atm.repository;

import com.sersaprosa.atm.model.BankUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private BankUser user1;
    private BankUser user2;

    @BeforeEach
    void setUp() {
        user1 = new BankUser();
        user1.setFirstName("Carlos Alexander");
        user1.setSecondName("De Leon Gutierrez");
        user1.setEmail("deleongutierrez7@gmail.com");
        user1.setPinNumber("3456");
        user2 = new BankUser();
        user2.setFirstName("Daniel Alberto");
        user2.setSecondName("Martinez Hernandez");
        user2.setEmail("martinezhernandez@gmail.com");
        user2.setPinNumber("6789");
        userRepository.saveAll(Arrays.asList(user1, user2));
    }
    @Test
    void findBankUserByUserName() {
        Optional<BankUser> result = userRepository.findBankUserByFirstName("Carlos Alexander");
        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
    }
    @Test
    void findBankUserByUserNameAndPassword() {
        Optional<BankUser> result = userRepository.findBankUserByFirstNameAndPinNumber("Carlos Alexander", "3456");
        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
    }

    @Test
    void findBankUserById() {
        Optional<BankUser> result = userRepository.findBankUserById(2);
        assertTrue(result.isPresent());
        assertEquals(user2, result.get());
    }
}