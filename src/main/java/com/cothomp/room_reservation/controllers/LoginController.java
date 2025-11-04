package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.data.UserRepository;
import com.cothomp.room_reservation.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/login")
    public String showLoginPage(
        @RequestParam(required = false) Integer roomId,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) String time,
        @RequestParam(required = false) Integer duration,
        HttpSession session) {
            
            if (roomId != null && date != null && time != null) {
                session.setAttribute("pendingRoomId", roomId);
                session.setAttribute("pendingDate", date);
                session.setAttribute("pendingTime", time);
                session.setAttribute("pendingDuration", duration != null ? duration : 30);
            }

            return "login";
        }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, 
                              @RequestParam String password, 
                              HttpSession session, 
                              Model model) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);

            Integer roomId = (Integer) session.getAttribute("pendingRoomId");
            String date = (String) session.getAttribute("pendingDate");
            String time = (String) session.getAttribute("pendingTime");
            Integer duration = (Integer) session.getAttribute("pendingDuration");
            
            if (roomId != null && date != null && time != null) {
                try {
                    LocalDate localDate = LocalDate.parse(date);
                    LocalTime localTime = LocalTime.parse(time);
                    LocalDateTime start = LocalDateTime.of(localDate, localTime);
                    LocalDateTime end = start.plusMinutes(duration != null ? duration : 30);

                    Room room = roomRepository.findById(roomId).orElse(null);
                    if (room != null) {
                        Reservation reservation = new Reservation();
                        reservation.setRoom(room);
                        reservation.setUser(user);
                        reservation.setStartTime(start);
                        reservation.setEndTime(end);
                        reservationRepository.save(reservation);
                    }
                } catch (Exception e) {
                    System.out.println("Error auto-reserving after login: " + e.getMessage());
                }
                
                session.removeAttribute("pendingRoomId");
                session.removeAttribute("pendingDate");
                session.removeAttribute("pendingTime");
                session.removeAttribute("pendingDuration");

                return "redirect:/reservations";
            }
                return "redirect:/";
            } else {
                model.addAttribute("error", true);
                return "login";
            }
        }
        
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            Object minCapacity = oldSession.getAttribute("minCapacity");
            Object floor = oldSession.getAttribute("floor");
            Object maxCapacity = oldSession.getAttribute("maxCapacity");
            Object requiresElevatorOrStairs = oldSession.getAttribute("requiresElevatorOrStairs");
            Object whiteboardFilter = oldSession.getAttribute("whiteboardFilter");
            Object projectorFilter = oldSession.getAttribute("projectorFilter");
            Object date = oldSession.getAttribute("date");
            Object time = oldSession.getAttribute("time");

            oldSession.invalidate();

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("minCapacity", minCapacity);
            newSession.setAttribute("floor", floor);
            newSession.setAttribute("maxCapacity", maxCapacity);
            newSession.setAttribute("requiresElevatorOrStairs", requiresElevatorOrStairs);
            newSession.setAttribute("whiteboardFilter", whiteboardFilter);
            newSession.setAttribute("projectorFilter", projectorFilter);
            newSession.setAttribute("date", date);
            newSession.setAttribute("time", time);
        }
        return "redirect:/";
    }
        
    @GetMapping("/reset")
    public String resetSession(HttpSession session) {
        session.setAttribute("justLoggedOut", true);
        return "redirect:/";
    }
}
