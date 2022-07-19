package com.misroservices.authentication.securities;

import com.misroservices.authentication.entities.Role;
import com.misroservices.authentication.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final static Logger log = LogManager.getLogger(JwtTokenProvider.class);

    private final JwtParser jwtParser;
    private final Key key;

    public JwtTokenProvider() {
        byte[] secretKey = Decoders.BASE64.decode("keyjndhjbhbvjdnhbvsbdhbfdsjhbsdfsdfsdfsdfsdfsdfsdfwerwedgfsdgvsdfaserfwe4rdsgsdfsdgfsdagfsdg");
        key = Keys.hmacShaKeyFor(secretKey);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateJwtToken(User user){
        String roles = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));
        long now = new Date().getTime();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("user_id", user.getId())
                .claim("roles", roles)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + 5*60*1000L))
                .compact();
    }

    public Authentication getAuthentication(String token, ServletRequest request){
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        //add userID in header request
        Long userID = Long.valueOf(claims.get("user_id").toString());
        request.setAttribute("user_id", userID);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if(!claims.get("roles").equals("")){
            authorities = Arrays.stream(claims.get("roles").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        org.springframework.security.core.userdetails.User user =
                new org.springframework.security.core.userdetails.User(claims.getSubject(),"", authorities);
        return new UsernamePasswordAuthenticationToken(user , token, authorities);
    }

    public Boolean validateToken(String jwtToken){
        try {
            Claims body = jwtParser.parseClaimsJws(jwtToken).getBody();
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
