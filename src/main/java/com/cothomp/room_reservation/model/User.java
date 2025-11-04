package com.cothomp.room_reservation.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "`user`")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    public User() {}

     public User(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    public Integer getId() {return id;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getFullName() {return fullName;}
    public String getEmail() {return email;}

    public void setId(Integer id) { this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public void setEmail(String email) {this.email = email;}
}
