package com.cothomp.room_reservation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Reservation {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    public Reservation() {}

    public Reservation(Room room, User user, LocalDateTime startTime, LocalDateTime endTime) {
        this.room = room;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() {return id;}
    public Room getRoom() {return room;}
    public User getUser() {return user;}
    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
   
    public void setId(Integer id) {this.id = id;}
    public void setRoom(Room room) {this.room = room;}
    public void setUser(User user) {this.user = user;}
    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}
    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

}



