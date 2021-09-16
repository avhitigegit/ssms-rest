package com.dev.ssms.component.jwt.service;

import com.dev.ssms.component.constant.AppConstant;
import com.dev.ssms.model.User;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    //Token generate at the time of user sign-up
    public String generateTokenWithPinAndEmail(User user) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateTokenWithPinAndEmail(claims, user.getEmailVerifiedCode(), user.getEmail());
    }

    private String doGenerateTokenWithPinAndEmail(Map<String, Object> claims, String pin, String email) {
        claims.put("generated-pin", pin);
//        claims.put("url", url);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(AppConstant.JSON_VALIDITY_PERIOD) * 1000))
                .signWith(SignatureAlgorithm.HS512, AppConstant.JSON_SECRET)
                .compact();
    }


    //Validate token
    public Boolean validateToken(String token, User user) {
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token));
    }

    //Retrieve username and pin from token which generate at the time of employee registration
    public List<String> getUserEmailAndPinFromToken(String token) {
        List<String> list = new ArrayList<>();
        Claims claims = Jwts.parser().setSigningKey(AppConstant.JSON_SECRET).parseClaimsJws(token).getBody();
        String pin = claims.get("generated-pin", String.class);
//        String url = claims.get("url", String.class);
        String userEmail = getClaimFromToken(token, Claims::getSubject);
        list.add(pin);
//        list.add(url);
        list.add(userEmail);
        return list;
    }


    //Retrieve username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //Retrieve expiration date from token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //Retrieve all claims
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(AppConstant.JSON_SECRET).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    //Check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    //Generate token for user email
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, user.getEmail());
    }

    //While creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //4. Compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String currentUserEmail) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(currentUserEmail)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(AppConstant.JSON_VALIDITY_PERIOD) * 1000))
                .signWith(SignatureAlgorithm.HS512, AppConstant.JSON_SECRET)
                .compact();
    }
}