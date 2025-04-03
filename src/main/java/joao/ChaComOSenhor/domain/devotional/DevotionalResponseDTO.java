package joao.ChaComOSenhor.domain.devotional;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerseResponseDTO;

import java.time.LocalDate;

public record DevotionalResponseDTO(Long id, String title, String content, LocalDate date, BibleVerseResponseDTO bibleVerse) {
}
