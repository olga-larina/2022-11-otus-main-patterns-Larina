package ru.otus.auth.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import ru.otus.auth.config.AppConfig;
import ru.otus.auth.dto.TokenDto;
import ru.otus.auth.exception.InvalidTokenException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class JwtServiceImpl implements JwtService {

    private final String issuer;
    private final Algorithm algorithm;

    public JwtServiceImpl(AppConfig appConfig) {
        this.issuer = appConfig.getIssuer();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String token(String userName, String gameId) {
        try {
            return JWT.create()
                .withIssuer(issuer)
                .withSubject(userName)
                .withClaim("game_id", gameId)
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new InvalidTokenException(exception);
        }
    }

    @Override
    public TokenDto verify(String token, String gameId) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaim("game_id", gameId)
                .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return new TokenDto(decodedJWT.getSubject(), gameId);
        } catch (JWTVerificationException exception){
            throw new InvalidTokenException(exception);
        }
    }
}
