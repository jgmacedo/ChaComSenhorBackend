package joao.ChaComOSenhor.domain.devotional;

public record ApiResponseDTO<T>(
    boolean success,
    T data,
    String message
) {
    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, null, message);
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, data, null);
    }
}