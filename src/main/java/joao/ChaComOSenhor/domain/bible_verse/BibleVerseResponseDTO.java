package joao.ChaComOSenhor.domain.bible_verse;

public record BibleVerseResponseDTO(
    Long id,
    String text,
    String reference,
    String book,
    Integer chapter,
    Integer verse
) {
    public ExactQuoteDTO toExactQuoteDTO() {
        return new ExactQuoteDTO(text, reference);
    }
}