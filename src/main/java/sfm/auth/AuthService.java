package sfm.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sfm.security.JwtService;
import sfm.users.*;

@Service
public class AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public String register(AuthDtos.RegisterRequest req) {
        if (users.existsByUsername(req.username())) throw new IllegalArgumentException("Username already taken");
        if (users.existsByEmail(req.email())) throw new IllegalArgumentException("Email already used");

        User u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));

        // default role: VIEWER (simple, safe)
        Role viewer = roles.findByName(RoleName.VIEWER).orElseThrow();
        u.getRoles().add(viewer);

        users.save(u);
        return jwt.createToken(u.getUsername());
    }

    public String login(AuthDtos.LoginRequest req) {
        User u = users.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwt.createToken(u.getUsername());
    }
}
