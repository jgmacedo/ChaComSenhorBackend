package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.logging.Logger;

@Service
public class DevotionalService {
    private static final Logger logger = Logger.getLogger(DevotionalService.class.getName());
    @Autowired
    private DevotionalRepository devotionalRepository;

    @Scheduled(cron = "0 1 0 * * *", zone = "America/Sao_Paulo")
    public void createDailyDevotional() {
        try {
            LocalDate today = LocalDate.now();
            // Only create if no devotional exists for today
            if(devotionalRepository.findByDate(today).isEmpty()) {
                Devotional devotional = new Devotional();
                devotional.setDate(today);

                // Get a random Bible verse
                BibleVerse verse = getRandomBibleVerse();
                devotional.setBibleVerse(verse);

                // Generate devotional title and content using ChatGPT
                String title = generateDevotionalTitle(verse);
                String content = generateDevotionalContent(verse);

                devotional.setTitle(title);
                devotional.setContent(content);

                devotionalRepository.save(devotional);
                logger.info("Devocional diária criada com sucesso para " + today);
            }
        } catch (Exception e) {
            logger.severe("Erro ao criar devocional diária: " + e.getMessage());
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
