package joao.ChaComOSenhor.domain.devotional;

import java.util.List;

public record SourceValidationDTO(
    List<String> sources,
    String ethicalAlignment
) {}