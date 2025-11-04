package com.cothomp.room_reservation.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cothomp.room_reservation.model.Room;
import java.util.List;
import org.springframework.data.repository.query.Param;


@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    // client (professor) requested that I find filtered rooms without first loading all the rooms (redundant), so
    // I had to create this Query. This selects all rooms where each filter matches, if a filter is null then ignore it
    @Query("""
        SELECT r FROM Room r 
        WHERE (:minSeats IS NULL OR r.seats >= COALESCE(:minSeats, r.seats))
        AND (:maxCap IS NULL OR r.maxCapacity >= COALESCE(:maxCap, r.maxCapacity))
        AND (:floor IS NULL OR r.floor = COALESCE(:floor, r.floor))
        AND (:requires IS NULL OR r.requiresElevatorOrStairs = COALESCE(:requires, r.requiresElevatorOrStairs))
        AND (:whiteboard IS NULL OR r.hasWhiteboard = COALESCE(:whiteboard, r.hasWhiteboard))
        AND (:projector IS NULL OR r.hasProjector = COALESCE(:projector, r.hasProjector))
        """)
        List<Room> findFiltered(
            @Param("minSeats") Integer minSeats,
            @Param("maxCap") Integer maxCap,
            @Param("floor") Integer floor,
            @Param("requires") Boolean requires,
            @Param("whiteboard") Boolean whiteboard,
            @Param("projector") Boolean projector
            );
}
