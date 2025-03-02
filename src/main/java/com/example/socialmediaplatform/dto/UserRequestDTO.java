package com.example.socialmediaplatform.dto;

import com.example.socialmediaplatform.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String username;
    private String email;
    private String password;
    private String profilePicture;
    private String bio;
    private String role="USER";

    public User toEntity(){
        return new User(null, this.username, this.email, this.password, this.profilePicture, this.bio ,null, null, null, null, null, "USER");
    }
}
