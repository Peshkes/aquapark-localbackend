package ru.kikopark.localbackend.configs;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.kikopark.localbackend.modules.authentication.service.AuthenticationService;
import ru.kikopark.localbackend.utils.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
@AllArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtService.getUsername(jwt);username = jwtService.getUsername(jwt);

                List<String> roles = jwtService.getRoles(jwt);

            } catch (ExpiredJwtException e) {

                // Если access token истек, пробуем обновить его с помощью refresh token
                String refreshToken = request.getHeader("Refresh-Token");
                username = jwtService.getUsername(refreshToken);
                if (username != null) {
                    // Если удалось извлечь username из refresh token, генерируем новый access token
                    UserDetails userDetails = null;
                    String newAccessToken = null;
                    try {
                        userDetails = authenticationService.loadUserByUsername(username);
                        newAccessToken = jwtService.generateAccessToken(userDetails);
                        response.setHeader("Authorization", newAccessToken);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtService.getRoles(jwt)
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
