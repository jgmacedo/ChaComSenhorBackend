package joao.ChaComOSenhor.domain.devotional;

import java.util.List;

public record ValidationDTO(
    List<String> sources,
    String ethicalAlignment
) {}