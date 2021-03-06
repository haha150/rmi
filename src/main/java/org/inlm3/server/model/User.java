package org.inlm3.server.model;

import org.inlm3.common.UserDTO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="User")
public class User implements UserDTO {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
