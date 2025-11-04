package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.model.Reservation;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class ReservationController {
    
    @Autowired 
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @PostMapping("/reserve/{roomId}")
    @ResponseBody
    public ResponseEntity<String> reserveRoom(
        @PathVariable Integer roomId,
        @RequestBody Map<String, String> payload,
        HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in to reserve a room.");
        }

        String date = payload.get("date");
        String time = payload.get("time");
        String durationStr = payload.get("duration");

        if (date == null || time == null) {
            return ResponseEntity.badRequest().body("Missing date or time.");
        }

        int durationMinutes = Integer.parseInt(durationStr);
        LocalDateTime startTime = LocalDateTime.parse(date + "T" + time);
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found.");
        }

        List<Reservation> overlaps = reservationRepository
                .findByRoom_IdAndEndTimeAfterAndStartTimeBefore(roomId, startTime, endTime);
        if (!overlaps.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("That time slot is already booked. Please choose another.");
        }

        Reservation reservation = new Reservation(room, user, startTime, endTime);
        reservationRepository.save(reservation);

        return ResponseEntity.ok("Room reserved successfully.");
    }

    @GetMapping("/reservations")   
    public String myReservations(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Reservation> mine = reservationRepository.findByUserOrderByStartTimeDesc(user);
        model.addAttribute("reservations", mine);
        model.addAttribute("loggedInUser", user.getUsername());
        return "reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        reservationRepository.findById(id).ifPresent(r -> {
            if (r.getUser().getId().equals(user.getId())) {
                reservationRepository.deleteById(id);
            }
        });
        return "redirect:/reservations";
    }
}
