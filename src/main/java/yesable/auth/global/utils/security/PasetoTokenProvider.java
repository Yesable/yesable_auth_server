package yesable.auth.global.utils.security;

import dev.paseto.jpaseto.*;
import dev.paseto.jpaseto.lang.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yesable.auth.module.enums.ValidateType;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class PasetoTokenProvider {
    private final KeyPair keyPair;

    public PasetoTokenProvider() {
        keyPair = Keys.keyPairFor(Version.V2);
    }

    /**
     * 사용자 id 값 추출
     */
    public String extractId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * claims 값 추출
     */
    public Claims getClaims(String token) {
        return parseToken(token).getClaims();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * authentication 으로 token 생성
     */
    public String generateToken(String userId) {
        Instant now = Instant.now();

        return Pasetos.V2.PUBLIC.builder()
                .setPrivateKey(this.keyPair.getPrivate())
                .setIssuedAt(now)
                .setExpiration(now.plus(2, ChronoUnit.HOURS))
                .setKeyId(UUID.randomUUID().toString())
                .setAudience("yesable")
                .setIssuer("auth")
                .claim("user_id", userId)
                .setSubject(userId)
                .compact();
    }

    /**
     * token parsing
     */
    public Paseto parseToken(String token) {
        PasetoParser parser = Pasetos.parserBuilder()
                .setPublicKey(keyPair.getPublic())
                .build();

        return parser.parse(token);
    }

    /**
     * token 값 검증
     */
    public ValidateType validateToken(String authToken) {
        try {
            parseToken(authToken);
            if(!isTokenExpired(authToken))
                return ValidateType.SUCCESS;
            else return ValidateType.EXPIRED_TIME;
        } catch (PasetoException e) {
            log.error("Token validation error: {}", e.getMessage());
            return ValidateType.FAILED;
        }
    }

    /**
     * == private ==
     * token 만료 시간 추출
     */
    private Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * == private ==
     * 토큰 만료 여부 조회
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }


}
