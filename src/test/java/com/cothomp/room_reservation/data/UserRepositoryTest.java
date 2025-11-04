package com.cothomp.room_reservation.data;

import com.cothomp.room_reservation.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsernameReturnsUserWhenExists() {
        User user = new User("colin", "1234", "Colin Thompson", "colin@example.com");
        userRepository.save(user);
        User found = userRepository.findByUsername("colin");
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("colin");
    }

    @Test
    void findByUsernameReturnsNullWhenNotFound() {
        User found = userRepository.findByUsername("missinguser");
        assertThat(found).isNull();
    }
}
