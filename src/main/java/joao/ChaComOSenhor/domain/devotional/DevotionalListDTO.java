package joao.ChaComOSenhor.domain.devotional;

import java.time.LocalDate;

public record DevotionalListDTO(Long id,
                                String title,
                                LocalDate date) {}
