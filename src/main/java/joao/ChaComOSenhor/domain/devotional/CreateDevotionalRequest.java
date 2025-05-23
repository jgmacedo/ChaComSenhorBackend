package joao.ChaComOSenhor.domain.devotional;

import java.time.LocalDate;

public record CreateDevotionalRequest(
    Long verseId,
    LocalDate date
) {
    public CreateDevotionalRequest {
        if (verseId == null) {
            throw new IllegalArgumentException("Verse ID cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create devotional for past dates");
        }
    }
}