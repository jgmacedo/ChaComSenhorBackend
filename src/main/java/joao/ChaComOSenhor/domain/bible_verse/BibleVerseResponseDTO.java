package joao.ChaComOSenhor.domain.bible_verse;

public record BibleVerseResponseDTO(Long id,
                                    String book,
                                    Integer chapter,
                                    Integer verse,
                                    String text,
                                    String reference) {}
