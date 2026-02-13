package sfm.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static sfm.auth.AuthDtos.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return new AuthResponse(auth.register(req));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return new AuthResponse(auth.login(req));
    }
}
