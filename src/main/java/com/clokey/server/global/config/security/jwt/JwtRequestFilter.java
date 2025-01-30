package com.clokey.server.global.config.security.jwt;

import java.io.IOException;

import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.global.config.security.auth.PrincipalDetails;
import com.clokey.server.global.config.security.auth.PrincipalDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final PrincipalDetailsService principalDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && authService.validateJwtToken(token)) {
            String email = authService.extractEmailFromToken(token);

            PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(email);

            if (principalDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principalDetails, null, principalDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 저장
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
