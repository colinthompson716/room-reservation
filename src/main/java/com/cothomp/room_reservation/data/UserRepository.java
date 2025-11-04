package com.cothomp.room_reservation.data;

import com.cothomp.room_reservation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // tells spring to handle all basic JPA database operations
    User findByUsername(String username);
}
