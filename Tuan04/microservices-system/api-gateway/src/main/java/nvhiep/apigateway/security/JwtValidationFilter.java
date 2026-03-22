package nvhiep.apigateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of("/auth/login", "/auth/register");
    private final SecretKey secretKey;

    public JwtValidationFilter(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String token = authHeader.substring(7);
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path);
    }
}
