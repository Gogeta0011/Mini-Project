package sfm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import sfm.users.User;
import sfm.users.UserRepository;

import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserRepository users;

    public JwtAuthFilter(JwtService jwt, UserRepository users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                String username = jwt.parseUsername(token);

                User u = users.findByUsername(username).orElse(null);
                if (u != null) {
                    var authorities = u.getRoles().stream()
                            .map(r -> "ROLE_" + r.getName().name())
                            .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ignored) {
            // keep request unauthenticated
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
