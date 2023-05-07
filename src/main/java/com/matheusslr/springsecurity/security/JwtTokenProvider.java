package com.matheusslr.springsecurity.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.matheusslr.springsecurity.domain.Role;
import com.matheusslr.springsecurity.dto.security.TokenDTO;
import com.matheusslr.springsecurity.repository.RoleRepository;
import com.matheusslr.springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String secret;
    @Value("${security.jwt.token.expire-length}")
    private Long expireTimeInMilli;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    public TokenDTO createAccessToken(String username, List<Role> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireTimeInMilli);
        String accessToken = getAccessToken(username, roles, now, validity);
        String refreshToken = getRefreshToken(username, roles, now);

        return TokenDTO.builder()
                .username(username)
                .created(now)
                .expiration(validity)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    public TokenDTO refreshToken(String refreshToken) {
        String formattedToken = formatToken(refreshToken);
        DecodedJWT decodedJWT = decodedToken(formattedToken);

        String username = decodedJWT.getSubject();
        var rolesAsString = decodedJWT.getClaim("roles").asList(String.class);
        List<Role> roles = new ArrayList<>();

        for(String role : rolesAsString){
            roles.add(roleRepository.findByDescription(role));
        }

        return createAccessToken(username, roles);
    }

    public String getAccessToken(String username, List<Role> roles, Date now, Date validity) {
        List<String> rolesAsString = new ArrayList<>();

        for (Role role : roles) {
            rolesAsString.add(role.getDescription());
        }

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("roles", rolesAsString)
                .sign(Algorithm.HMAC256(secret));
    }

    public String getRefreshToken(String username, List<Role> roles, Date now) {
        Date validity = new Date(now.getTime() + expireTimeInMilli * 2);
        List<String> rolesAsString = new ArrayList<>();

        for (Role role : roles) {
            rolesAsString.add(role.getDescription());
        }

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("roles", rolesAsString)
                .sign(Algorithm.HMAC256(secret));
    }

    public String formatToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String formattedToken = token.substring("Bearer ".length());
            return formattedToken;
        }
        return null;
    }

    private DecodedJWT decodedToken(String token) {
        JWTVerifier jwtVerifier = JWT
                .require(Algorithm.HMAC256(secret))
                .build();
        return jwtVerifier.verify(token);
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        if (token != null && new Date().before(decodedJWT.getExpiresAt())) {
            return true;
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = userService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
