package com.matheusslr.springsecurity.repository;

import com.matheusslr.springsecurity.domain.RememberMeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class JpaPersistentRememberMeTokenRepository implements PersistentTokenRepository {
    @Autowired
    private RememberMeTokenRepository tokenRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeToken rememberMeToken = RememberMeToken.builder()
                .username(token.getUsername())
                .tokenValue(token.getTokenValue())
                .createAt(token.getDate())
                .expiresIn(new Date(token.getDate().getTime() + 86400000))
                .build();
        tokenRepository.save(rememberMeToken);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMeToken rememberMeToken = tokenRepository.findByUsername(series);
        if (rememberMeToken != null) {
            rememberMeToken.setTokenValue(tokenValue);
            rememberMeToken.setCreateAt(lastUsed);
            tokenRepository.save(rememberMeToken);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        RememberMeToken rememberMeToken = tokenRepository.findById(Long.valueOf(seriesId)).get();
        if (rememberMeToken != null) {
            return new PersistentRememberMeToken(rememberMeToken.getUsername(), rememberMeToken.getUsername(), rememberMeToken.getTokenValue(), rememberMeToken.getCreateAt());
        }
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        RememberMeToken rememberMeToken = tokenRepository.findByUsername(username);
        if(rememberMeToken != null)
            tokenRepository.delete(rememberMeToken);
    }
}
