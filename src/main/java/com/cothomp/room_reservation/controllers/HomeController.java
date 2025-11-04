package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.data.UserRepository;
import com.cothomp.room_reservation.model.Reservation;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.User;

import jakarta.servlet.http.HttpSession;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/")
    public String showHome(@RequestParam(required = false) Integer minCapacity, 
                           @RequestParam(required = false) Integer floor,
                           @RequestParam(required = false) Integer maxCapacity, 
                           @RequestParam(required = false) String requiresElevatorOrStairs,
                           @RequestParam(required = false) String whiteboardFilter,
                           @RequestParam(required = false) String projectorFilter,
                           @RequestParam(required = false) String time, 
                           @RequestParam(required = false) String date, 
                           HttpSession session, Model model, 
                           @RequestParam(required = false, defaultValue = "false") boolean showAll,
                           @RequestParam(required = false, defaultValue = "30") Integer duration,
                           @RequestParam(required = false, defaultValue = "false") String advancedUsed
                           ) {
        
        if (session.getAttribute("justLoggedOut") != null) {
            session.removeAttribute("justLoggedOut");
            showAll = false;
            session.removeAttribute("showAll");
        }
        
        List<Room> rooms = null;

        if (session.getAttribute("justHidAll") != null) {
            session.removeAttribute("justHidAll");
            rooms = null;
            model.addAttribute("rooms", null);
            model.addAttribute("showAll", false);
            model.addAttribute("hasAdvancedFilter", false);
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            model.addAttribute("loggedInUser", loggedInUser != null ? loggedInUser.getUsername() : null);
            return "index";
        }

        if (minCapacity == null && floor == null && maxCapacity == null && requiresElevatorOrStairs == null 
            && whiteboardFilter == null && projectorFilter == null && time == null && date == null 
            && session != null && session.getAttribute("loggedInUser") != null) {

                minCapacity = (Integer) session.getAttribute("minCapacity");
                floor = (Integer) session.getAttribute("floor");
                maxCapacity = (Integer) session.getAttribute("maxCapacity");
                requiresElevatorOrStairs = (String) session.getAttribute("requiresElevatorOrStairs");
                whiteboardFilter = (String) session.getAttribute("whiteboardFilter");
                projectorFilter = (String) session.getAttribute("projectorFilter");
                date = (String) session.getAttribute("date");
                time = (String) session.getAttribute("time");
            }
        final Integer minCapFinal = minCapacity;
        final Integer floorFinal = floor;
        final Integer maxCapFinal = maxCapacity;

        final Boolean requires = 
        (requiresElevatorOrStairs != null && !requiresElevatorOrStairs.isBlank()) ? Boolean.parseBoolean(requiresElevatorOrStairs) : null;
        
        final Boolean whiteboard =
        (whiteboardFilter != null && !whiteboardFilter.isBlank()) ? Boolean.parseBoolean(whiteboardFilter) : null;
        
        final Boolean projector =
        (projectorFilter != null && !projectorFilter.isBlank()) ? Boolean.parseBoolean(projectorFilter) : null;
        
        boolean hasUserFilters =
            (minCapacity != null || floor != null || maxCapacity != null ||
            requiresElevatorOrStairs != null || whiteboard != null ||
            projector != null || (date != null && !date.isBlank()) ||
            (time != null && !time.isBlank()));

    /*  boolean hasAdvancedFilter = floor != null 
            || maxCapacity != null 
            || requiresElevatorOrStairs != null
            || whiteboard != null
            || projector != null;
            */

        boolean hasAdvancedFilter = "true".equals(advancedUsed);

        model.addAttribute("hasAdvancedFilter", hasAdvancedFilter);
        
        if (showAll) {
            rooms = roomRepository.findAll();
            session.setAttribute("showAll", true);
        } else if (hasUserFilters) {
            rooms = roomRepository.findFiltered(
                minCapFinal,
                maxCapFinal,
                floorFinal,
                requires,
                whiteboard,
                projector
            );
            
            // filtering rooms based on if they are already booked at a given time or date
            if (date != null && !date.isBlank() && time != null && !time.isBlank()) {
                try {
                    LocalDate localDate = LocalDate.parse(date);
                    String[] parts = time.split(":");
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);
                    LocalTime localTime = LocalTime.of(hour, minute);
                    LocalDateTime start = LocalDateTime.of(localDate, localTime);
                    LocalDateTime end = start.plusMinutes(duration);

                    List<Reservation> conflicts = reservationRepository.findByEndTimeAfterAndStartTimeBefore(start, end);
                    Set<Integer> bookedRoomIds = conflicts.stream()
                            .map(r -> r.getRoom().getId())
                            .collect(Collectors.toSet());

                    rooms = rooms.stream()
                            .filter(r -> !bookedRoomIds.contains(r.getId()))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    System.out.println("Error parsing time: " + e.getMessage());
                }
            }
        } else {
            rooms = null;
            session.removeAttribute("showAll");
        }

        // adding and setting attributes so that they remain on the filter
        model.addAttribute("rooms", rooms);
        model.addAttribute("showAll", showAll);

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser != null ? loggedInUser.getUsername() : null);

        model.addAttribute("minCapacity", minCapacity);
        model.addAttribute("floor", floor);
        model.addAttribute("maxCapacity", maxCapacity);
        model.addAttribute("requiresElevatorOrStairs", requiresElevatorOrStairs);
        model.addAttribute("time", time);
        model.addAttribute("date", date);
        model.addAttribute("whiteboardFilter", whiteboardFilter);
        model.addAttribute("projectorFilter", projectorFilter);
        
        session.setAttribute("minCapacity", minCapacity);
        session.setAttribute("floor", floor);
        session.setAttribute("maxCapacity", maxCapacity);
        session.setAttribute("requiresElevatorOrStairs", requiresElevatorOrStairs);
        session.setAttribute("whiteboardFilter", whiteboardFilter);
        session.setAttribute("projectorFilter", projectorFilter);
        session.setAttribute("date", date);
        session.setAttribute("time", time);
        
        return "index";
    }

    // function for "List all rooms" button
    @GetMapping("/rooms/all")
    public String listAllRooms(Model model, HttpSession session) {
        return "redirect:/?showAll=true";
    }

    // function for "Hide rooms" button
    @GetMapping("/rooms/hide")
    public String hideAllRooms(HttpSession session) {
        session.removeAttribute("showAll");
        session.setAttribute("justHidAll", true);
        return "redirect:/";
    }
}
