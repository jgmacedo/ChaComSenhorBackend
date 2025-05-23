package joao.ChaComOSenhor.domain.bible_verse;

import java.time.LocalDateTime;

public record BibleVerseCreationDTO(
        String reference,
        String text
) {
    public BibleVerse toBibleVerse() {
        BibleVerse bibleVerse = new BibleVerse();
        bibleVerse.setReference(reference);
        bibleVerse.setText(text);
        bibleVerse.setCreationDate(LocalDateTime.now());
        return bibleVerse;
    }
}