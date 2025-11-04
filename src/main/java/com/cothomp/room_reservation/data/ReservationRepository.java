package com.cothomp.room_reservation.data;

import com.cothomp.room_reservation.model.Reservation;
import com.cothomp.room_reservation.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {    
    // for "My Reservations" page
    List<Reservation> findByUserOrderByStartTimeDesc(User user);

    // checking for overlapping reservations
    List<Reservation> findByRoom_IdAndEndTimeAfterAndStartTimeBefore(Integer roomId, LocalDateTime newStart, LocalDateTime newEnd);

    // find all reservations for a room on a specific day
    List<Reservation> findByRoom_IdAndStartTimeBetween(Integer roomId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    // finds all reservations that interfere with a new one's time window 
    List<Reservation> findByEndTimeAfterAndStartTimeBefore(LocalDateTime start, LocalDateTime end);

}
