package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.logging.Logger;

@Service
public class DevotionalService {
    private static final Logger logger = Logger.getLogger(DevotionalService.class.getName());
    private final DevotionalRepository devotionalRepository;
    private final AiService aiService;
    private final BibleVerseRepository bibleVerseRepository;
    private final ResponseBuilderService responseBuilderService;


    @Autowired
    public DevotionalService(DevotionalRepository devotionalRepository,
                             AiService aiService, BibleVerseRepository bibleVerseRepository, ResponseBuilderService responseBuilderService) {
        this.devotionalRepository = devotionalRepository;
        this.aiService = aiService;
        this.bibleVerseRepository = bibleVerseRepository;
        this.responseBuilderService = responseBuilderService;
    }

    @Transactional
    public Devotional createDailyDevotional(BibleVerse bibleVerse) {
        try {
            if (checkIfDevotionalExistsForToday()) {
                logger.info("A devotional for today already exists");
                return null;
            }

            Devotional devotional = aiService.generateDevotional(bibleVerse);
            devotional.setDate(LocalDate.now());
            devotional.setBibleVerse(bibleVerse);

            return devotionalRepository.save(devotional);
        } catch (Exception e) {
            logger.severe("Error creating daily devotional: " + e.getMessage());
            throw new RuntimeException("Failed to create daily devotional: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Devotional saveDevotional(Devotional devotional) {
        try {
            return devotionalRepository.save(devotional);
        } catch (Exception e) {
            logger.severe("Error saving devotional: " + e.getMessage());
            throw new RuntimeException("Failed to save devotional", e);
        }
    }

    public boolean checkIfDevotionalExistsForToday() {
        return devotionalRepository.findByDate(LocalDate.now()).isPresent();
    }

    public Devotional getTodaysDevotional() {
        return devotionalRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Today's devotional not found"));
    }

    public Devotional getDevotionalByDate(LocalDate date) {
        return devotionalRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Devotional not found for date: " + date));
    }

    @Transactional
    public Devotional generateCompleteDevotional(Long verseId) throws Exception {
        BibleVerse bibleVerse = findBibleVerseOrThrow(verseId);
        Devotional devotional = aiService.generateDevotional(bibleVerse);

        // Ensure all fields are properly set
        devotional.setBibleVerse(bibleVerse);
        devotional.setDate(LocalDate.now());

        return devotional;
    }

    private BibleVerse findBibleVerseOrThrow(Long id) throws Exception {
        return bibleVerseRepository.findById(id)
                .orElseThrow(() -> new Exception("Bible verse not found"));
    }
}