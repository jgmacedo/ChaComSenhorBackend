package joao.ChaComOSenhor.domain.bible_verse;

public record BibleVerseCreationDTO(
        String reference,
        String text
) {
    public BibleVerse toBibleVerse() {
        BibleVerse bibleVerse = new BibleVerse();
        bibleVerse.setReference(reference);
        bibleVerse.setText(text);
        return bibleVerse;
    }
}