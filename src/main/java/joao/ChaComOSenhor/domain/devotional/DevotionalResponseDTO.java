package joao.ChaComOSenhor.domain.devotional;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;

import java.time.LocalDate;

public record DevotionalResponseDTO(
    Long id,
    String title,
    String reflection,
    String prayer,
    String practicalApplication,
    String supportingVerses,
    LocalDate date,
    BibleVerse bibleVerse
    ) {}