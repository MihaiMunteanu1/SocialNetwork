package org.example.llab67.domain;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime friendsFrom;
    private Long id;

    public FriendshipDTO(Long id,String firstName, String lastName, String email, LocalDateTime friendsFrom) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.friendsFrom = friendsFrom;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {
        return "FriendshipDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", friendsFrom=" + friendsFrom +
                ", id=" + id +
                '}';
    }
}