package joao.ChaComOSenhor.domain.bible_verse;

public record BibleVerseCreationDTO(
        String book,
        Integer chapter,
        Integer verse,
        String text
) {
    public BibleVerse toBibleVerse() {
        BibleVerse bibleVerse = new BibleVerse();
        bibleVerse.setBook(book);
        bibleVerse.setChapter(chapter);
        bibleVerse.setVerse(verse);
        bibleVerse.setText(text);
        return bibleVerse;
    }
}