package com.cothomp.room_reservation;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class RoomReservationApplication {

	@PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Indiana/Indianapolis"));
        System.out.println("Default timezone set to America/Indiana/Indianapolis");
    }

	public static void main(String[] args) {
		SpringApplication.run(RoomReservationApplication.class, args);
	}
}
