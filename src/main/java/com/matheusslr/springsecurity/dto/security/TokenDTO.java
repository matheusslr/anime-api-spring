package com.matheusslr.springsecurity.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenDTO {
    private String username;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenDTO token = (TokenDTO) o;
        return Objects.equals(username, token.username) && Objects.equals(authenticated, token.authenticated) && Objects.equals(created, token.created) && Objects.equals(expiration, token.expiration) && Objects.equals(accessToken, token.accessToken) && Objects.equals(refreshToken, token.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authenticated, created, expiration, accessToken, refreshToken);
    }
}
