package com.cothomp.room_reservation.data;

import com.cothomp.room_reservation.model.Reservation;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private UserRepository userRepo;

    private User user;
    private Room room;

    // lets data reset before each tests runs
    @BeforeEach
    void setup() {
        user = new User("colin", "1234", "Colin", "colin@example.com");
        userRepo.save(user);

        room = new Room("Luddy 1001", 10, 20, 1, false, true, false, false);
        roomRepo.save(room);
    }

    @Test
    void findByUserOrderByStartTimeDescReturnsSortedList() {
        Reservation r1 = new Reservation(room, user,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1));
        Reservation r2 = new Reservation(room, user,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        reservationRepo.saveAll(List.of(r1, r2));

        List<Reservation> results = reservationRepo.findByUserOrderByStartTimeDesc(user);
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getStartTime()).isAfter(results.get(1).getStartTime());
    }

    @Test
    void findByRoomIdAndEndTimeAfterAndStartTimeBeforeDetectsOverlap() {
        LocalDateTime now = LocalDateTime.now();
        Reservation existing = new Reservation(room, user,
                now.plusHours(1), now.plusHours(3));
        reservationRepo.save(existing);

        List<Reservation> overlaps = reservationRepo.findByRoom_IdAndEndTimeAfterAndStartTimeBefore(
                room.getId(), now.plusHours(2), now.plusHours(4));

        assertThat(overlaps).isNotEmpty();
    }

    @Test
    void findByRoomIdAndStartTimeBetweenFindsSameDayReservations() {
        LocalDateTime startOfDay = LocalDateTime.of(2025, 11, 3, 0, 0);
        LocalDateTime endOfDay = LocalDateTime.of(2025, 11, 3, 23, 59);
        Reservation sameDay = new Reservation(room, user,
                LocalDateTime.of(2025, 11, 3, 9, 0),
                LocalDateTime.of(2025, 11, 3, 10, 0));
        Reservation otherDay = new Reservation(room, user,
                LocalDateTime.of(2025, 11, 4, 9, 0),
                LocalDateTime.of(2025, 11, 4, 10, 0));
        reservationRepo.saveAll(List.of(sameDay, otherDay));

        List<Reservation> results = reservationRepo.findByRoom_IdAndStartTimeBetween(
                room.getId(), startOfDay, endOfDay);
        assertThat(results).containsExactly(sameDay);
    }

    @Test
    void findByEndTimeAfterAndStartTimeBeforeFindsOngoingReservations() {
        LocalDateTime now = LocalDateTime.now();
        Reservation ongoing = new Reservation(room, user,
                now.minusMinutes(30), now.plusMinutes(30));
        Reservation past = new Reservation(room, user,
                now.minusHours(2), now.minusHours(1));
        reservationRepo.saveAll(List.of(ongoing, past));

        List<Reservation> results = reservationRepo.findByEndTimeAfterAndStartTimeBefore(
                now, now.plusHours(1));

        assertThat(results).contains(ongoing).doesNotContain(past);
    }
}
