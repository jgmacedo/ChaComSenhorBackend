package joao.ChaComOSenhor.domain.bible_verse;

public record BibleVerseResponseDTO(
    Long id,
    String text,
    String reference
) {
    public ExactQuoteDTO toExactQuoteDTO() {
        return new ExactQuoteDTO(text, reference);
    }
}