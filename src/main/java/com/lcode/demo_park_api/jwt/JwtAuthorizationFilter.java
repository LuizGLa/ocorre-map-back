package com.lcode.demo_park_api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService detailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isSwaggerRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = request.getHeader(JwtUtils.JWT_AUTHORIZATION);

        if (token == null || !token.startsWith(JwtUtils.JWT_BEARER)) {
            log.info("JWT Token está nulo, vazio ou não iniciado com 'Bearer '.");
            filterChain.doFilter(request, response);
            return;
        }

        if (!JwtUtils.isTokenValid(token)) {
            log.warn("JWT Token está inválido ou expirado.");
            filterChain.doFilter(request, response);
            return;
        }

        String username = JwtUtils.getUsernameFromToken(token);
        Long userId = JwtUtils.getUserIdFromToken(token);

        toAuthentication(request, username, userId);

        filterChain.doFilter(request, response);
    }

    private boolean isSwaggerRequest(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs/");
    }

    private void toAuthentication(HttpServletRequest request, String username, Long userId) {
        UserDetails userDetails = detailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        request.setAttribute("userId", userId); // Armazena o ID do usuário na requisição para uso posterior
    }
}