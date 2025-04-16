package joao.ChaComOSenhor.domain.user;

public record UserResponseDTO(
        String name,
        String login,
        String email,
        String role
) {}