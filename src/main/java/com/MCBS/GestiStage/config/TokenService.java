package com.MCBS.GestiStage.config;

import com.MCBS.GestiStage.dtos.response.AuthResponseDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }
    public AuthResponseDTO generateJwtToken(String eamil,
                                               Collection<? extends GrantedAuthority> authorities,
                                               boolean withRefreshToken){
        Map<String,String > idToken=new HashMap<>();
        Instant instant=Instant.now();
        String scope=authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(instant)
                .expiresAt(instant.plus(withRefreshToken?180:300, ChronoUnit.MINUTES))
                .subject(eamil)
                .claim("scope",scope)
                .build();
        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("accessToken",accessToken);
        if(withRefreshToken){
            JwtClaimsSet jwtRefreshTokenClaimsSet=JwtClaimsSet.builder()
                    .issuer("auth-service")
                    .issuedAt(instant)
                    .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                    .subject(eamil)
                    .build();
            String refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwtRefreshTokenClaimsSet)).getTokenValue();
            idToken.put("refreshToken",refreshToken);
        }
        AuthResponseDTO authResponseDTO= new AuthResponseDTO(   accessToken,
                                                                idToken.get("refreshToken"),
                                                                eamil,
                                                                scope
                                                                );
        return authResponseDTO;
    }
}
