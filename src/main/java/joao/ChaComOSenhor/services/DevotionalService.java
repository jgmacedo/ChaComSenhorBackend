package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.logging.Logger;

@Service
public class DevotionalService {
    private static final Logger logger = Logger.getLogger(DevotionalService.class.getName());

    @Autowired
    private DevotionalRepository devotionalRepository;

    @Autowired
    private AiService aiService;

    public Devotional createDailyDevotional(BibleVerse bibleVerse) {
        try {
            checkIfDevotionalExistsForToday();

            Devotional content = generateDevotionalContent(bibleVerse);

            Devotional devotional = buildDevotional(content, bibleVerse);

            devotionalRepository.save(devotional);
            logger.info("Devocional diária criada com sucesso para " + LocalDate.now());

            return devotional;
        } catch (Exception e) {
            logger.severe("Erro ao criar devocional diária: " + e.getMessage());
            throw new RuntimeException("Failed to create daily devotional: " + e.getMessage(), e);
        }
    }

    private void checkIfDevotionalExistsForToday() {
        if (devotionalRepository.findByDate(LocalDate.now()).isPresent()) {
            throw new RuntimeException("A devotional for today already exists");
        }
    }

    private Devotional generateDevotionalContent(BibleVerse bibleVerse) {
        String jsonResponse = aiService.sendPostToOpenRouter(bibleVerse);
        return aiService.parseJsonToDevotional(jsonResponse);
    }

    private Devotional buildDevotional(Devotional content, BibleVerse bibleVerse) {
        Devotional devotional = new Devotional();
        devotional.setTitle(content.getTitle());
        devotional.setReflection(content.getReflection());
        devotional.setPrayer(content.getPrayer());
        devotional.setPracticalApplication(content.getPracticalApplication());
        devotional.setSupportingVerses(content.getSupportingVerses());
        devotional.setDate(LocalDate.now());
        devotional.setBibleVerse(bibleVerse);
        return devotional;
    }

    public Devotional getTodaysDevotional() {
        LocalDate today = LocalDate.now();
        return devotionalRepository.findByDate(today)
                .orElseThrow(() -> new RuntimeException("Não encontramos a devocional de hoje."));
    }

    public Devotional getDevotionalByDate(LocalDate date) {
        return devotionalRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Não encontramos a devocional do dia " + date.toString()));
    }
}