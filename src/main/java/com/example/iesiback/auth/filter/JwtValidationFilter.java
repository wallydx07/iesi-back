package com.example.iesiback.auth.filter;

import static com.example.iesiback.auth.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.iesiback.auth.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        String header = request.getHeader(HEADER_AUTHORIZATION);
//
//        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.replace(PREFIX_TOKEN, "");
//        try {
//            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
//            String username = claims.getSubject();
//            // String username2 = (String) claims.get("username");
//            Object authoritiesClaims = claims.get("authorities");
//
//            Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
//            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
//                    .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
//
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null,
//                    roles);
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            chain.doFilter(request, response);
//
//        } catch (JwtException e) {
//            Map<String, String> body = new HashMap<>();
//            body.put("error", e.getMessage());
//            body.put("message", "El token es invalido!");
//
//            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
//            response.setStatus(401);
//            response.setContentType(CONTENT_TYPE);
//        }
//
//    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        System.out.println("════════════════ JWT FILTER INICIO ════════════════");

        String header = request.getHeader(HEADER_AUTHORIZATION);
        System.out.println("HEADER Authorization = " + header);

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            System.out.println("❌ No hay token o no empieza con Bearer");
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(PREFIX_TOKEN, "");
        System.out.println("TOKEN = " + token.substring(0, Math.min(40, token.length())) + "...");

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("CLAIMS = " + claims);

            String username = claims.getSubject();
            System.out.println("USERNAME = " + username);

            Object authoritiesClaims = claims.get("authorities");
            System.out.println("AUTHORITIES RAW = " + authoritiesClaims);

            Collection<? extends GrantedAuthority> roles =
                    Arrays.stream(new ObjectMapper()
                                    .addMixIn(SimpleGrantedAuthority.class,
                                            SimpleGrantedAuthorityJsonCreator.class)
                                    .readValue(authoritiesClaims.toString(),
                                            SimpleGrantedAuthority[].class))
                            .toList();

            System.out.println("AUTHORITIES PARSED = " + roles);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, roles);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("AUTH SET EN CONTEXT = " + authentication);

            chain.doFilter(request, response);

        } catch (JwtException e) {
            System.out.println("❌ JWT ERROR: " + e.getMessage());

            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token es invalido!");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(CONTENT_TYPE);
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(body)
            );
        }

        System.out.println("════════════════ JWT FILTER FIN ════════════════");
    }

}
