//package com.sklep.sklep_backend.config;
//
//
//
//import com.sklep.sklep_backend.service.impl.JWTUtilsImpl;
//import com.sklep.sklep_backend.service.impl.OurUserDetailsServiceImpl;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@AllArgsConstructor
//public class JWTAuthFilter extends OncePerRequestFilter {
//
//
//    private JWTUtilsImpl jwtUtils;
//
//
//    private OurUserDetailsServiceImpl ourUserDetailsService;
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        final String jwtToken;
//        final String userEmail;
//
//        String header = request.getHeader("Authorization");
//        System.out.println("header: "+header);
//
//        if (authHeader == null || authHeader.isBlank()) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwtToken = authHeader.substring(7);
//        userEmail = jwtUtils.extractUsername(jwtToken);
//
//        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail);
//
//            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
//                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities()
//                );
//                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                securityContext.setAuthentication(token);
//                SecurityContextHolder.setContext(securityContext);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}


package com.sklep.sklep_backend.config;

import com.sklep.sklep_backend.service.impl.JWTUtilsImpl;
import com.sklep.sklep_backend.service.impl.OurUserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtilsImpl jwtUtils;
    private final OurUserDetailsServiceImpl userDetailsService;

    public JWTAuthFilter(JWTUtilsImpl jwtUtils,
                         OurUserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        System.out.println("\n=== JWTAuthFilter :: NEW REQUEST =======================");
        System.out.println("URI.................: " + request.getRequestURI());

        String header = request.getHeader("Authorization");
        System.out.println("Authorization header: " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println(">>> Header brak / zły prefix  → przepuszczam dalej");
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        System.out.println("Raw JWT token.......: " + token);

        String email = null;
        try {
            email = jwtUtils.extractUsername(token);
            System.out.println("Extracted username..: " + email);
        } catch (Exception ex) {
            System.out.println(">>> NIE UDAŁO SIĘ WYCIĄGNĄĆ USERNAME: " + ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad token");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("Loaded user.........: " + userDetails.getUsername());
                System.out.println("Authorities.........: " + userDetails.getAuthorities());
            } catch (Exception ex) {
                System.out.println(">>> NIE ZNALEZIONO UŻYTKOWNIKA: " + ex);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            boolean valid = jwtUtils.isTokenValid(token, userDetails);
            System.out.println("Token valid.........: " + valid);

            if (valid) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext sc = SecurityContextHolder.createEmptyContext();
                sc.setAuthentication(auth);
                SecurityContextHolder.setContext(sc);

                System.out.println(">>> Authentication ustawione w SecurityContext");
            } else {
                System.out.println(">>> TOKEN NIEPOPRAWNY");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalid");
                return;
            }
        } else {
            System.out.println(">>> Już uwierzytelniony lub email==null");
        }

        chain.doFilter(request, response);
    }
}
