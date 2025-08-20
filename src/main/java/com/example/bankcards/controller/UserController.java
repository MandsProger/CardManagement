package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Получить имя своего пользователя */
    @GetMapping("/me")
    public ResponseEntity<String> getMyUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        return ResponseEntity.ok("Current user: " + username);
    }

    /** Получить выбранного пользователя (только для ADMIN) */
    @GetMapping("/find/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getCurrentUser(@PathVariable Long userId) {
        String user = userService.getCurrentUsers(userId);
        return ResponseEntity.ok(user);
    }

    /** Получить всех пользователей (только для ADMIN) */
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /** Изменить роль пользователя (только для ADMIN) */
    @PutMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String roleStr = body.get("role");
        Role newRole = Role.valueOf(roleStr);
        UserDTO user = userService.changeUserRole(id, newRole);
        return ResponseEntity.ok(user);
    }

    /** Удаление пользователя (только для ADMIN) */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}