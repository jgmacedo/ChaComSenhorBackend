package joao.ChaComOSenhor.domain.devotional;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;

import java.time.LocalDate;

public record DevotionalCreatorDTO(
        Long id,
        String title,
        String reflection,
        String prayer,
        String practicalApplication,
        String supportingVerses,
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
                devotional.getSupportingVerses(),
                devotional.getDate(),
                devotional.getBibleVerse()
        );
    }
}