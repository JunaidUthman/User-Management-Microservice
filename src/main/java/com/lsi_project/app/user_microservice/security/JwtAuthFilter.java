package com.lsi_project.app.user_microservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to process JWT token included in the Authorization header.
 */
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CostumeUserService costumeUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String walletAddress; // Used as the 'username'

        // 1. Check for token presence
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // 2. Extract user (wallet address) from token
            walletAddress = jwtUtil.extractUsername(jwt);

            // 3. Check security context and token validity
            if (walletAddress != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.costumeUserService.loadUserByUsername(walletAddress);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    // Token is valid, update security context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials are not needed
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token is invalid or expired
            System.err.println("JWT processing failed: " + e.getMessage());
            SecurityContextHolder.clearContext();
            // Allow the request to proceed to handle 401/403 later
        }

        filterChain.doFilter(request, response);
    }
}