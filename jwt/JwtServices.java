package com.smoothstack.lms.common.jwt;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtServices {
    private Logger logger = LoggerFactory.getLogger(JwtServices.class);

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    @Value("${jwt.secret:generate}")
    private String signingKey;

    private String generateNewSecretKey() {

        SecretKey key =  Keys.secretKeyFor(signatureAlgorithm);
        return Encoders.BASE64.encode(key.getEncoded()).toUpperCase();

    }

    public String getSigningKey() {
        if (!signingKey.startsWith("HS512:")) {
            String key = generateNewSecretKey();
            logger.warn("=======================================================================");
            logger.warn(" Warning: The JWT secret key will be invalid when restart server.      ");
            logger.warn("          Please add the following key to your application.properties. ");
            logger.warn("=======================================================================");
            logger.warn(String.format("jwt.secret = HS512:%s", key ));
            return key;
        } else {
            return signingKey.replaceAll("^HS512:","");
        }
    }


    public JsonWebToken getJasonWebToken(UserDetails userDetails) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String serializedAuthorities = objectMapper.writeValueAsString(userDetails.getAuthorities());

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSigningKey()));

        String signedToken = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Smoothstack, Inc.")
                .setSubject(userDetails.getUsername())
                .claim("authorities", serializedAuthorities)
                .signWith(secretKey)
                .compact();

        return new JsonWebToken(signedToken);
    }

    public JwtParser getJwtParser() {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSigningKey()));
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }

    public UserDetails getUserDetails(JsonWebToken jsonWebToken, Function<Jws<Claims>, UserDetails> extractFunction) {
        Jws<Claims> jws = getJwtParser().parseClaimsJws(jsonWebToken.getToken());

        return extractFunction.apply(jws);
    }

    public static UserDetails extractFromTrustedSource(Jws<Claims> jws)  {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User.UserBuilder userBuilder = User.withUsername(jws.getBody().getSubject());
            userBuilder.password(jws.getSignature());

            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(jws.getBody().get("authorities").toString());
            JsonNode node = mapper.readTree(parser);

            Set<String> authorities = new HashSet<>();
            node.iterator().forEachRemaining(
                    s -> authorities.add(s.get("authority").asText())
            );

            userBuilder.authorities(authorities.toArray(new String[0]));

            return userBuilder.build();
        }
        catch (Exception ignored) {}
        return null;
    }
}
