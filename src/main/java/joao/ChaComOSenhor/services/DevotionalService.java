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
                             AiService aiService, BibleVerseRepository bibleVerseRepository) {
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
    public Devotional generateCompleteDevotional(Long verseId, LocalDate date) {
        BibleVerse bibleVerse = bibleVerseRepository.findById(verseId)
                .orElseThrow(() -> new ResourceNotFoundException("Bible verse not found with id: " + verseId));

        if (checkIfDevotionalExistsForDate(date)) {
            throw new IllegalStateException("A devotional already exists for date: " + date);
        }

        try {
            Devotional devotional = aiService.generateDevotional(bibleVerse);
            devotional.setBibleVerse(bibleVerse);
            devotional.setDate(date);
            return devotional;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate devotional: " + e.getMessage(), e);
        }
    }

    public List<LocalDate> getAllDevotionalDates() {
        List<LocalDate> dates = devotionalRepository.findAllDates();
        if (dates.isEmpty()) {
            throw new RuntimeException("No devotional dates found");
        }
        return dates;
    }
}