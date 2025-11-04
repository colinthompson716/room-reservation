package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.model.Reservation;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.User;
import com.cothomp.room_reservation.config.TestViewResolverConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@Import(TestViewResolverConfig.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @Test
    void reserveRoomRequiresLogin() throws Exception {
        mockMvc.perform(post("/reserve/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2025-11-03\",\"time\":\"09:00\",\"duration\":\"60\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reserveRoomReturnsBadRequestIfMissingDateOrTime() throws Exception {
        User user = new User("colin", "1234", "Colin", "colin@example.com");
        mockMvc.perform(post("/reserve/1")
                .sessionAttr("loggedInUser", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":null,\"time\":null,\"duration\":\"60\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveRoomReturnsBadRequestIfRoomNotFound() throws Exception {
        User user = new User("colin", "1234", "Colin", "colin@example.com");
        when(roomRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(post("/reserve/1")
                .sessionAttr("loggedInUser", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2025-11-03\",\"time\":\"09:00\",\"duration\":\"60\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveRoomDetectsOverlap() throws Exception {
        User user = new User("colin", "1234", "Colin", "colin@example.com");
        Room room = new Room("Luddy 0113", 6, 8, 1, false, true, false, false);
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(reservationRepository.findByRoom_IdAndEndTimeAfterAndStartTimeBefore(anyInt(), any(), any()))
                .thenReturn(List.of(new Reservation()));

        mockMvc.perform(post("/reserve/1")
                .sessionAttr("loggedInUser", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2025-11-03\",\"time\":\"09:00\",\"duration\":\"60\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void reserveRoomSucceeds() throws Exception {
        User user = new User("colin", "1234", "Colin", "colin@example.com");
        Room room = new Room("Luddy 0113", 6, 8, 1, false, true, false, false);
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(reservationRepository.findByRoom_IdAndEndTimeAfterAndStartTimeBefore(anyInt(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/reserve/1")
                .sessionAttr("loggedInUser", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2025-11-03\",\"time\":\"09:00\",\"duration\":\"60\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Room reserved successfully."));

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void myReservationsRedirectsIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void myReservationsShowsListIfLoggedIn() throws Exception {
        User user = new User("colin", "1234", "Colin", "colin@example.com");

        Room room = new Room("Luddy 1001", 10, 20, 1, false, true, false, false);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(1));

        List<Reservation> reservations = List.of(reservation);
        when(reservationRepository.findByUserOrderByStartTimeDesc(user)).thenReturn(reservations);

        mockMvc.perform(get("/reservations").sessionAttr("loggedInUser", user))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("reservations"))
                .andExpect(model().attribute("loggedInUser", "colin"));
        }

    @Test
    void cancelReservationRedirectsIfNotLoggedIn() throws Exception {
        mockMvc.perform(post("/reservations/1/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void cancelReservationDeletesIfOwnerMatches() throws Exception {
        User user = new User("colin", "1234", "Colin", "colin@example.com");
        user.setId(1);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(1));

        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));

        mockMvc.perform(post("/reservations/1/cancel")
                .sessionAttr("loggedInUser", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations"));

        verify(reservationRepository, times(1)).deleteById(1);
    }
}
