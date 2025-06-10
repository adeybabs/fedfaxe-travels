package com.project.fedfaxe.config.security;

import com.project.fedfaxe.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // ✅ Validate the token before extracting the username
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = User.withUsername(username).password("").roles("USER").build();
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                // 🔴 Invalid JWT: Return 401 Unauthorized instead of redirecting
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
                response.getWriter().flush();
                return;
            }
        }

        // ✅ If authentication is missing and it's not the login endpoint, return 401
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!request.getRequestURI().startsWith("/oauth2/authorize/google")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized: Please log in\"}");
                response.getWriter().flush();
                return;
            } else {
                // 🔄 Redirect to Google OAuth only if it's the login endpoint
                response.sendRedirect("https://accounts.google.com/signin");
                return;
            }
        }
        // Proceed with the request if authentication is valid
        chain.doFilter(request, response);
    }
}
