package com.matheusslr.springsecurity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tb_remember_me_token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RememberMeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(name = "token_value", nullable = false)
    private String tokenValue;
    @Column(name = "create_at", nullable = false)
    private Date createAt;
    @Column(name = "expires_in", nullable = false)
    private Date expiresIn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RememberMeToken that = (RememberMeToken) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(tokenValue, that.tokenValue) && Objects.equals(createAt, that.createAt) && Objects.equals(expiresIn, that.expiresIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, tokenValue, createAt, expiresIn);
    }
}
