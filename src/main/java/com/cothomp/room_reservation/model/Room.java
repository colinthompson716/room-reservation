package com.cothomp.room_reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roomName;
    private int seats;
    private int maxCapacity;
    private int floor;
    
    private boolean requiresElevatorOrStairs;
    private boolean available;
    private boolean hasWhiteboard;
    private boolean hasProjector;

    public Room() {};

    public Room(String roomName, int seats, 
                int maxCapacity, int floor, 
                boolean requiresElevatorOrStairs, boolean available,
                boolean hasWhiteboard, boolean hasProjector) {
        this.roomName = roomName;
        this.seats = seats; 
        this.maxCapacity = maxCapacity;
        this.floor = floor;
        this.requiresElevatorOrStairs = requiresElevatorOrStairs;
        this.available = available;
        this.hasWhiteboard = hasWhiteboard;   
        this.hasProjector = hasProjector;   
    }

    public Integer getId()                     {return id;}
    public String getRoomName()             {return roomName;}
    public int getSeats()                   {return seats;}
    public int getMaxCapacity()             {return maxCapacity;}
    public int getFloor()                   {return floor;}
    public boolean getRequiresElevatorOrStairs()    {return requiresElevatorOrStairs;}
    public boolean getAvailable()           {return available;}
    public boolean getHasWhiteboard()       {return hasWhiteboard;}
    public boolean getHasProjector()        {return hasProjector;}

    public void setId                       (Integer id)                   {this.id = id;}
    public void setRoomName                 (String roomName)           {this.roomName = roomName;}
    public void setSeats                    (int seats)                 {this.seats = seats;}
    public void setMaxCapacity              (int maxCapacity)           {this.maxCapacity = maxCapacity;}
    public void setFloor                    (int floor)                 {this.floor = floor;}
    public void setRequiresElevatorOrStairs (boolean requiresElevatorOrStairs)  {this.requiresElevatorOrStairs = requiresElevatorOrStairs;}
    public void setAvailable                (boolean available)         {this.available = available;}
    public void setHasWhiteboard            (boolean hasWhiteboard)     {this.hasWhiteboard = hasWhiteboard;}
    public void setHasProjector             (boolean hasProjector)      {this.hasProjector = hasProjector;}
}
