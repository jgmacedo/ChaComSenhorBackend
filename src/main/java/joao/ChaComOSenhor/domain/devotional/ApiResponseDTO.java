package joao.ChaComOSenhor.domain.devotional;

import java.util.Map;

public record ApiResponseDTO<T>(boolean success, T data) {
    public static ApiResponseDTO<Object> error(String message) {
        return new ApiResponseDTO<>(false, Map.of("error", message));
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, data);
    }
}