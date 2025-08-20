package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private Set<Role> roles;

    public static UserDTO fromEntity(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles()
        );
    }
}
