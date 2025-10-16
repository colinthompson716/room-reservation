package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.model.Reservation;
import com.cothomp.room_reservation.model.Room;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ReservationController {
    
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // showing the form, login is required
   @GetMapping("/reserve/{roomId}")
    public String showReserveForm(@PathVariable Long roomId, @RequestParam(required = false) String date, HttpSession session, Model model) {
        String user = (String) session.getAttribute("loggedInUser");
        if (user == null) { 
            return "redirect:/login";
        }

        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            model.addAttribute("error", "Room not found");
            return "redirect:/rooms";
        }

        List<String> timeSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 0);

        while (!start.isAfter(end.minusMinutes(30))) {
            timeSlots.add(start.toString());
            start = start.plusMinutes(30);
        }


        if (date != null && !date.isEmpty()) {
            LocalDate selectedDate = LocalDate.parse(date);
            List<Reservation> reservations = reservationRepository.findByRoom_IdAndStartTimeBetween(
                roomId, 
                selectedDate.atStartOfDay(), 
                selectedDate.plusDays(1).atStartOfDay());

            for (Reservation r : reservations) {
                LocalTime reservedStart = r.getStartTime().toLocalTime();
                timeSlots.remove(reservedStart.truncatedTo(ChronoUnit.MINUTES).toString());
            }
        }
        model.addAttribute("room", room);
        model.addAttribute("timeSlots", timeSlots);
        model.addAttribute("loggedInUser", user);
        return "reserve_form";
    }

    // handl users submitting the form
    @PostMapping("/reserve")
    public String createReservation(@RequestParam Long roomId, @RequestParam String date, @RequestParam String time, @RequestParam int durationMinutes, HttpSession session, Model model) {
        String user = (String) session.getAttribute("loggedInUser");
        if (user == null) { 
            return "redirect:/login";
        }

        Room room = roomRepository.findById(roomId).orElse(null);

        if (room == null) {
            model.addAttribute("error", "Room not found.");
            return "redirect:/rooms";
        }

        // build start time and end time
        LocalDate d = LocalDate.parse(date);
        LocalTime t = LocalTime.parse(time);

        if (t.isBefore(LocalTime.of(8,0)) || t.isAfter(LocalTime.of(20,0))) {
            model.addAttribute("error", "Start time must be between 08:00 and 20:00.");
            model.addAttribute("room", room);
            model.addAttribute("timeSlots", generateTimeSlots());
            return "reserve_form";
        }

        LocalDateTime start = LocalDateTime.of(d, t);
        LocalDateTime end = start.plusMinutes(durationMinutes);

        // make sure reservations are between 8am and 8pm
        if (end.toLocalTime().isAfter(LocalTime.of(20,0))) {
            model.addAttribute("error", "Reservation must end by 20:00.");
            model.addAttribute("room", room);
            model.addAttribute("timeSlots", generateTimeSlots());
            return "reserve_form";
        }

        // check for overlap in room reservations
        List<Reservation> overlaps = 
            reservationRepository.findByRoom_IdAndEndTimeAfterAndStartTimeBefore(room.getId(), start, end);
        if (!overlaps.isEmpty()) {
            model.addAttribute("error", "That time is already booked. Choose another slot.");
            model.addAttribute("room", room);
            model.addAttribute("timeSlots", generateTimeSlots());
            return "reserve_form";
        }
        reservationRepository.save(new Reservation(room, user, start, end));
        return "redirect:/reservations";
    }

    // my reservations by user
    @GetMapping("/reservations")   
    public String myReservations(HttpSession session, Model model) {
        String user = (String) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        List<Reservation> mine = reservationRepository.findByUsernameOrderByStartTimeDesc(user);
        model.addAttribute("reservations", mine);
        model.addAttribute("loggedInUser", user);
        return "reservations";
    }

    // cancel the reservation
    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, HttpSession session) {
        String user = (String) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        reservationRepository.findById(id).ifPresent(r -> {
            if (user.equals(r.getUsername())) {
                reservationRepository.deleteById(id);
            }
        });
        return "redirect:/reservations";
    }

    // generates all 30 minute timeslots for time slot dropdown bar
    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 0);
        while (!start.isAfter(end.minusMinutes(30))) {
            timeSlots.add(start.toString());
            start = start.plusMinutes(30);
        }
        return timeSlots;
    }
}
