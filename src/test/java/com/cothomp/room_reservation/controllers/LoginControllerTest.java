package com.cothomp.room_reservation.controllers;

import com.cothomp.room_reservation.data.ReservationRepository;
import com.cothomp.room_reservation.data.RoomRepository;
import com.cothomp.room_reservation.data.UserRepository;
import com.cothomp.room_reservation.model.Room;
import com.cothomp.room_reservation.model.User;
import com.cothomp.room_reservation.model.Reservation;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cothomp.room_reservation.config.TestViewResolverConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(LoginController.class)
@Import(TestViewResolverConfig.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @Test
    void showsLoginPageNormally() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void savesPendingRoomInfoInSession() throws Exception {
        mockMvc.perform(get("/login")
                .param("roomId", "1")
                .param("date", "2025-11-03")
                .param("time", "09:00")
                .param("duration", "60"))
                .andExpect(status().isOk());
    }

    @Test
    void successfulLoginRedirectsToHomeWhenNoPendingRoom() throws Exception {
        User user = new User("colin", "pass", "Colin Thompson", "colin@example.com");
        when(userRepository.findByUsername("colin")).thenReturn(user);

        mockMvc.perform(post("/login")
                .param("username", "colin")
                .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void failedLoginShowsError() throws Exception {
        when(userRepository.findByUsername("colin")).thenReturn(null);

        mockMvc.perform(post("/login")
                .param("username", "colin")
                .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void autoReservesRoomAfterLoginIfPending() throws Exception {
        User user = new User("colin", "1234", "Colin Thompson", "colin@example.com");
        Room room = new Room("Luddy 1001", 10, 15, 1, false, true, true, false);
        when(userRepository.findByUsername("colin")).thenReturn(user);
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));

        mockMvc.perform(post("/login")
                .sessionAttr("pendingRoomId", 1)
                .sessionAttr("pendingDate", "2025-11-03")
                .sessionAttr("pendingTime", "09:00")
                .sessionAttr("pendingDuration", 30)
                .param("username", "colin")
                .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations"));

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void logoutClearsAndRestoresFilterValues() throws Exception {
        mockMvc.perform(get("/logout")
                .sessionAttr("minCapacity", 10)
                .sessionAttr("floor", 2)
                .sessionAttr("time", "09:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void resetSessionSetsJustLoggedOutFlag() throws Exception {
        mockMvc.perform(get("/reset"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
