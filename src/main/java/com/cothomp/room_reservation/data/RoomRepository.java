package com.cothomp.room_reservation.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cothomp.room_reservation.model.Room;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // search query
    List<Room> findByRoomNameContainingIgnoreCase(String keyword);
    
}
