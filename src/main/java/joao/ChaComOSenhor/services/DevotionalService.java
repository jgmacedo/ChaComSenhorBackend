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
    private AiService aiService;

    public Devotional createDailyDevotional(BibleVerse bibleVerse) {
        try {
            LocalDate today = LocalDate.now();

            // Check if a devotional already exists for today
            if (devotionalRepository.findByDate(today).isPresent()) {
                throw new RuntimeException("A devotional for today already exists");
            }

            Devotional devotional = new Devotional();
            devotional.setDate(today);
            devotional.setBibleVerse(bibleVerse);

            // First generate title
            String title = aiService.generateDevotionalTitle(bibleVerse);
            // Then pass title to content generation
            String content = aiService.generateDevotionalContent(bibleVerse, title);

            devotional.setTitle(title);
            devotional.setContent(content);

            devotionalRepository.save(devotional);
            logger.info("Devocional diária criada com sucesso para " + today);

            return devotional;
        } catch (Exception e) {
            logger.severe("Erro ao criar devocional diária: " + e.getMessage());
            throw new RuntimeException("Failed to create daily devotional: " + e.getMessage(), e);
        }
    }

    public Devotional getTodaysDevotional(){
        LocalDate today = LocalDate.now();
        return devotionalRepository.findByDate(today)
                .orElseThrow(() -> new RuntimeException("Não encontramos a devocional de hoje."));
    }
    public Devotional getDevotionalByDate(LocalDate date){
        return devotionalRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Não encontramos a devocional do dia " + date.toString()));
    }


}
