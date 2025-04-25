package joao.ChaComOSenhor.domain.user;

import java.util.List;

public record LoginResponseDTO(
        String token,
        String user,
        List<String> roles
) {
}