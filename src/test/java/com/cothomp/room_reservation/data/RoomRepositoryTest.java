package com.cothomp.room_reservation.data;

import com.cothomp.room_reservation.model.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private RoomRepository repo;

    @Test
    void findFilteredReturnsExpectedRooms() {
        Room r1 = new Room("Luddy 1010", 10, 20, 1, false, true, false, false);
        Room r2 = new Room("Luddy 2020", 25, 40, 2, true, true, true, false);
        repo.saveAll(List.of(r1, r2));

        List<Room> results = repo.findFiltered(10, 20, 1, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRoomName()).isEqualTo("Luddy 1010");
    }

    @Test
    void findFilteredReturnsAllWhenAllParamsNull() {
        Room room = new Room("Luddy 3003", 8, 15, 3, false, true, false, false);
        repo.save(room);

        List<Room> results = repo.findFiltered(null, null, null, null, null, null);
        assertThat(results).isNotEmpty();
    }

    @Test
    void findFilteredHandlesNoMatches() {
        Room r1 = new Room("Luddy 4004", 6, 10, 4, false, false, false, false);
        repo.save(r1);

        List<Room> results = repo.findFiltered(100, 500, 1, null, null, null);
        assertThat(results).isEmpty();
    }
}
