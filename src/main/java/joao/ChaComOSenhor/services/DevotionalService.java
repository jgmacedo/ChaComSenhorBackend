package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.exceptions.ResourceNotFoundException;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class DevotionalService {
    private static final Logger logger = Logger.getLogger(DevotionalService.class.getName());
    private final DevotionalRepository devotionalRepository;
    private final AiService aiService;
    private final BibleVerseRepository bibleVerseRepository;
    private final ExecutorService executor = Executors.newCachedThreadPool();


    @Autowired
    public DevotionalService(DevotionalRepository devotionalRepository,
                             AiService aiService, BibleVerseRepository bibleVerseRepository) {
        this.devotionalRepository = devotionalRepository;
        this.aiService = aiService;
        this.bibleVerseRepository = bibleVerseRepository;
    }

    @Transactional
    public CompletableFuture<Devotional> saveDevotional(Devotional devotional) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return devotionalRepository.save(devotional);
            } catch (Exception e) {
                logger.severe("Error saving devotional: " + e.getMessage());
                throw new RuntimeException("Failed to save devotional", e);
            }
        }, executor);
    }

    @Transactional(readOnly = true)
    public boolean checkIfDevotionalExistsForDate(LocalDate date) {
        return devotionalRepository.findByDate(date).isPresent();
    }

    public Devotional getTodaysDevotional() {
        return devotionalRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Today's devotional not found"));
    }

    @Transactional(readOnly = true)
    public Devotional getDevotionalByDate(LocalDate date) {
        return devotionalRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Devotional not found for date: " + date));
    }

    @Transactional
    public CompletableFuture<Devotional> generateCompleteDevotional(Long verseId, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
            BibleVerse bibleVerse = bibleVerseRepository.findById(verseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bible verse not found with id: " + verseId));

            if (checkIfDevotionalExistsForDate(date)) {
                throw new IllegalStateException("A devotional already exists for date: " + date);
            }
            return bibleVerse;
        }, executor).thenCompose(bibleVerse ->
            aiService.generateDevotional((BibleVerse) bibleVerse)
                .thenApply(devotional -> {
                    devotional.setBibleVerse((BibleVerse) bibleVerse);
                    devotional.setDate(date);
                    return devotional;
                })
        ).exceptionally(e -> { throw new RuntimeException("Failed to generate devotional: " + e.getMessage(), e); });
    }

    public List<LocalDate> getAllDevotionalDates() {
        List<LocalDate> dates = devotionalRepository.findAllDates();
        dates.sort(Comparator.reverseOrder());
        if (dates.isEmpty()) {
            throw new RuntimeException("No devotional dates found");
        }
        return dates;
    }
}