package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.Reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @Test
    void loadsHomePage() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"));
    }

    @Test
    void redirectsListAllRooms() throws Exception {
        mockMvc.perform(get("/rooms/all"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void redirectsHideAllRooms() throws Exception {
        mockMvc.perform(get("/rooms/hide"))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    void showsAllRoomsWhenShowAllTrue() throws Exception {
        List<Room> rooms = List.of(new Room("Luddy 0001", 20, 25, 1, false, true, true, false));

        when(roomRepository.findAll()).thenReturn(rooms);

        mockMvc.perform(get("/").param("showAll", "true"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeExists("rooms"));

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void filtersRoomsWhenParamsProvided() throws Exception {
        List<Room> rooms = List.of(
            new Room("Luddy 0002", 15, 20, 1, false, true, true, true)
            );
            when(roomRepository.findFiltered(eq(10), eq(30), eq(1), any(), any(), any())).thenReturn(rooms);
            mockMvc.perform(get("/")
                    .param("minCapacity", "10")
                    .param("maxCapacity", "30")
                    .param("floor", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("rooms"));
                
                verify(roomRepository, times(1)).findFiltered(eq(10), eq(30), eq(1), any(), any(), any());
            }

    @Test
    void excludesRoomsWithConflictingReservations() throws Exception {
        Room room = new Room("Luddy 0003", 10, 15, 1, false, true, true, true);
        room.setId(1);
        
        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.parse("2025-11-03T10:00"));
        reservation.setEndTime(LocalDateTime.parse("2025-11-03T11:00"));

        when(roomRepository.findFiltered(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(room));
        when(reservationRepository.findByEndTimeAfterAndStartTimeBefore(any(), any()))
                .thenReturn(List.of(reservation));

        mockMvc.perform(get("/")
                .param("date", "2025-11-03")
                .param("time", "10:30")
                .param("duration", "30"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attributeExists("rooms"));

        verify(reservationRepository, times(1)).findByEndTimeAfterAndStartTimeBefore(any(), any());
    }
}       
