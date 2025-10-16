package com.cothomp.room_reservation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Reservation {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;


    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    public Reservation() {}

    public Reservation(Room room, String username, LocalDateTime startTime, LocalDateTime endTime) {
        this.room = room;
        this.username = username;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {return id;}
    public Room getRoom() {return room;}
    public String getUsername() {return username;}
    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
   
    public void setId(Long id) {this.id = id;}
    public void setRoom(Room room) {this.room = room;}
    public void setUsername(String username) {this.username = username;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

}



