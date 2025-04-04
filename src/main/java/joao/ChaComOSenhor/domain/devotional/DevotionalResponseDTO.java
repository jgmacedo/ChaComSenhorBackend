package joao.ChaComOSenhor.domain.devotional;

import joao.ChaComOSenhor.domain.bible_verse.ExactQuoteDTO;

import java.time.LocalDate;

public record DevotionalResponseDTO(
    Long id,
    ExactQuoteDTO exactQuote,
    String title,
    String reflection,
    String prayer,
    PracticalApplicationDTO practicalApplication,
    ValidationDTO validation,
    LocalDate date
) {}