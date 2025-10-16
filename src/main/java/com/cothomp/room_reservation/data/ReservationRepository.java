package com.cothomp.room_reservation.data;

import com.cothomp.room_reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // for "My Reservations" page
    List<Reservation> findByUsernameOrderByStartTimeDesc(String username);

    // checking for overlapping reservations
    List<Reservation> findByRoom_IdAndEndTimeAfterAndStartTimeBefore(Long roomId, LocalDateTime newStart, LocalDateTime newEnd);

    // find all reservations for a room on a specific day
    List<Reservation> findByRoom_IdAndStartTimeBetween(Long roomId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
