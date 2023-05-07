package com.matheusslr.springsecurity.controller;

import com.matheusslr.springsecurity.domain.User;
import com.matheusslr.springsecurity.dto.security.AccountCredentials;
import com.matheusslr.springsecurity.dto.security.TokenDTO;
import com.matheusslr.springsecurity.repository.RoleRepository;
import com.matheusslr.springsecurity.repository.UserRepository;
import com.matheusslr.springsecurity.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    @PutMapping("/refresh/{username}")
    public ResponseEntity refresh(
            @PathVariable(value = "username") String username,
            @RequestHeader("Authorization") String refreshToken
            ) {
        var user = userRepository.findByUsername(username);

        if(user == null)
            return new ResponseEntity("Username do not exist", HttpStatus.BAD_REQUEST);

        var token = jwtTokenProvider.refreshToken(refreshToken);

        if(token == null)
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(token);
    }
}
