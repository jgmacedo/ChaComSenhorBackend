package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevotionalServiceTest {

    @Mock
    private DevotionalRepository devotionalRepository;

    @Mock
    private AiService aiService;

    @InjectMocks
    private DevotionalService devotionalService;

    private BibleVerse bibleVerse;

    @BeforeEach
    void setUp() {
        bibleVerse = new BibleVerse();
        bibleVerse.setReference("John 3:16");
        bibleVerse.setText("For God so loved the world...");
    }

    @Test
    void createDailyDevotional_createsDevotionalSuccessfully() {
        when(devotionalRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());
        when(aiService.generateDevotionalTitle(any(BibleVerse.class))).thenReturn("God's Love");
        when(aiService.generateDevotionalContent(any(BibleVerse.class), anyString())).thenReturn(new Devotional());

        Devotional devotional = devotionalService.createDailyDevotional(bibleVerse);

        assertNotNull(devotional);
        assertEquals("God's Love", devotional.getTitle());
        verify(devotionalRepository, times(1)).save(any(Devotional.class));
    }

    @Test
    void createDailyDevotional_throwsExceptionIfDevotionalExists() {
        when(devotionalRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(new Devotional()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            devotionalService.createDailyDevotional(bibleVerse);
        });

        assertEquals("Failed to create daily devotional: A devotional for today already exists", exception.getMessage());
        verify(devotionalRepository, never()).save(any(Devotional.class));
    }

    @Test
    void getTodaysDevotional_returnsDevotionalIfExists() {
        Devotional devotional = new Devotional();
        when(devotionalRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(devotional));

        Devotional result = devotionalService.getTodaysDevotional();

        assertNotNull(result);
        assertEquals(devotional, result);
    }

    @Test
    void getTodaysDevotional_throwsExceptionIfNotFound() {
        when(devotionalRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            devotionalService.getTodaysDevotional();
        });

        assertEquals("Não encontramos a devocional de hoje.", exception.getMessage());
    }

    @Test
    void getDevotionalByDate_returnsDevotionalIfExists() {
        LocalDate date = LocalDate.of(2025, 4, 4);
        Devotional devotional = new Devotional();
        when(devotionalRepository.findByDate(date)).thenReturn(Optional.of(devotional));

        Devotional result = devotionalService.getDevotionalByDate(date);

        assertNotNull(result);
        assertEquals(devotional, result);
    }

    @Test
    void getDevotionalByDate_throwsExceptionIfNotFound() {
        LocalDate date = LocalDate.of(2025, 4, 4);
        when(devotionalRepository.findByDate(date)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            devotionalService.getDevotionalByDate(date);
        });

        assertEquals("Não encontramos a devocional do dia 2025-04-04", exception.getMessage());
    }
}