package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.data.RoomRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class RoomController {
    
    @Autowired
    public RoomRepository roomRepository;

    @GetMapping("/rooms")
    public String getAllRooms(@RequestParam(required = false) String keyword, HttpSession session, Model model) {
        List<Room> rooms;
        if (keyword != null && !keyword.isEmpty()) {
            rooms = roomRepository.findByRoomNameContainingIgnoreCase(keyword);
        } else {
            rooms = roomRepository.findAll();
        }

        model.addAttribute("rooms", rooms);
        model.addAttribute("keyword", keyword);

        String user = (String) session.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", user);

        return "rooms";
    }
}

