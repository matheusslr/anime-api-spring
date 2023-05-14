package com.matheusslr.springsecurity.controller;

import com.matheusslr.springsecurity.domain.User;
import com.matheusslr.springsecurity.dto.security.AccountCredentials;
import com.matheusslr.springsecurity.repository.RoleRepository;
import com.matheusslr.springsecurity.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication endpoint")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Operation(
            summary = "Register a common user",
            description = "Register a user with low-level access credentials",
            tags = "Authentication endpoint",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AccountCredentials credentials) {
        var user = userRepository.findByUsername(credentials.getUsername());
        if (user == null) {
            var role = roleRepository.findByDescription("ROLE_USER");
            var userSaved = User.builder()
                    .username(credentials.getUsername())
                    .password(new BCryptPasswordEncoder().encode(credentials.getPassword()))
                    .roles(List.of(role))
                    .isAccountNonExpired(true)
                    .isCredentialsNonExpired(true)
                    .isAccountNonLocked(true)
                    .isEnabled(true)
                    .build();

            userRepository.save(userSaved);
            return ResponseEntity.ok("User Registered!");
        } else {
            return new ResponseEntity("Username already taken!", HttpStatus.BAD_REQUEST);
        }
    }
}
