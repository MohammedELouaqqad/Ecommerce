package com.example.demo.config;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthentificationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String requestPath = request.getServletPath();
        String method = request.getMethod();
        
        System.out.println("🔍 JwtAuthenticationFilter: " + method + " " + requestPath);
        
        try {
            final String authHeader = request.getHeader("Authorization");
            
            // Pas de token = OK pour endpoints permitAll()
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("ℹ️  No Bearer token found - continuing without authentication");
                filterChain.doFilter(request, response);
                return;
            }

            // Token trouvé - extraire et valider
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);
            
            System.out.println("📌 Token extracted for user: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ Authentication set for: " + userEmail);
                } else {
                    System.out.println("⚠️  Token invalid for user: " + userEmail);
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ JWT Filter Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
