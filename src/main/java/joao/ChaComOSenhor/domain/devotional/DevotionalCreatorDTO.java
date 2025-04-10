package joao.ChaComOSenhor.domain.devotional;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record DevotionalCreatorDTO(
        Long id,
        String title,
        String reflection,
        String prayer,
        String practicalApplication,
        List<String> supportingVerses,
        LocalDate date,
        BibleVerse bibleVerse
) {
    public static DevotionalCreatorDTO fromDevotional(Devotional devotional) {
        return new DevotionalCreatorDTO(
                devotional.getId(),
                devotional.getTitle(),
                devotional.getReflection(),
                devotional.getPrayer(),
                devotional.getPracticalApplication(),
                Collections.singletonList(devotional.getSupportingVerses()),
                devotional.getDate(),
                devotional.getBibleVerse()
        );
    }
}