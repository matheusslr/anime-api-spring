package com.matheusslr.springsecurity.controller;

import com.matheusslr.springsecurity.domain.Anime;
import com.matheusslr.springsecurity.domain.User;
import com.matheusslr.springsecurity.dto.security.AccountCredentials;
import com.matheusslr.springsecurity.dto.security.TokenDTO;
import com.matheusslr.springsecurity.repository.RoleRepository;
import com.matheusslr.springsecurity.repository.UserRepository;
import com.matheusslr.springsecurity.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication endpoint")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    @Operation(
            summary = "Authenticates a user",
            description = "Authenticates a user and returns a token",
            tags = "Authentication endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TokenDTO.class))
                            )}),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AccountCredentials accountCredentials) {
        String username = accountCredentials.getUsername();
        String password = accountCredentials.getPassword();

        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = userRepository.findByUsername(username);
        if (user != null) {
            TokenDTO token = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRoles());
            return ResponseEntity.ok(token);
        } else {
            return new ResponseEntity<>("Invalid username/password!", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Refresh token",
            description = "Refresh token for authenticated user and returns a new token",
            tags = "Authentication endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TokenDTO.class))
                            )}),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    @PutMapping("/refresh/{username}")
    public ResponseEntity refresh(
            @PathVariable(value = "username") String username,
            @RequestHeader("Authorization") String refreshToken,
            Authentication authentication
    ) {
        String currentUsername = authentication.getName();
        if(!currentUsername.equals(username))
            return new ResponseEntity("You are not authorized to update another user's token", HttpStatus.FORBIDDEN);

        var user = userRepository.findByUsername(username);

        if (user == null)
            return new ResponseEntity("Username do not exist", HttpStatus.BAD_REQUEST);

        var token = jwtTokenProvider.refreshToken(refreshToken);

        if (token == null)
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(token);
    }
}
