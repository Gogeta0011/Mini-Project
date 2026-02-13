package sfm.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank @Size(min=3,max=64) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(min=6,max=128) String password
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(String token) {}
}
