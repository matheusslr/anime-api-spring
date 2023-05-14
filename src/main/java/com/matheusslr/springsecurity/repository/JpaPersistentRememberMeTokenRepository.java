package com.matheusslr.springsecurity.repository;

import com.matheusslr.springsecurity.domain.RememberMeToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@Log4j2
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
        try {
            RememberMeToken rememberMeToken = tokenRepository.findById(Long.valueOf(seriesId))
                    .orElse(null);
            if (rememberMeToken != null) {
                return new PersistentRememberMeToken(
                        rememberMeToken.getUsername(),
                        seriesId,
                        rememberMeToken.getTokenValue(),
                        rememberMeToken.getCreateAt());
            }
        }catch (InvalidDataAccessApiUsageException e){
            log.info("Invalid format token or cannot find in database");
        }
        finally {
            return null;
        }
    }

    @Override
    public void removeUserTokens(String username) {
        RememberMeToken rememberMeToken = tokenRepository.findByUsername(username);
        if (rememberMeToken != null)
            tokenRepository.delete(rememberMeToken);
    }
}
