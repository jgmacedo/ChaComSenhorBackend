package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
public class DevotionalService {
    private static final Logger logger = Logger.getLogger(DevotionalService.class.getName());
    private final DevotionalRepository devotionalRepository;
    private final AiService aiService;
    private final BibleVerseRepository bibleVerseRepository;


    @Autowired
    public DevotionalService(DevotionalRepository devotionalRepository,
                             AiService aiService, BibleVerseRepository bibleVerseRepository, ResponseBuilderService responseBuilderService) {
        this.devotionalRepository = devotionalRepository;
        this.aiService = aiService;
        this.bibleVerseRepository = bibleVerseRepository;
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

    public boolean checkIfDevotionalExistsForDate(LocalDate date) {
        return devotionalRepository.findByDate(date).isPresent();
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

    public List<LocalDate> getAllDevotionalDates() {
        List<LocalDate> dates = devotionalRepository.findAllDates();
        if (dates.isEmpty()) {
            throw new RuntimeException("No devotional dates found");
        }
        return dates;
    }
}