package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerseCreationDTO;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.UserRepository;
import joao.ChaComOSenhor.services.DevotionalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/admin")
public class AdminController {

    private final DevotionalService devotionalService;
    private UserRepository userRepository;
    private BibleVerseRepository bibleVerseRepository;

    public AdminController(DevotionalService devotionalService) {
        this.devotionalService = devotionalService;
    }

    @GetMapping("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get_bible_verse_by_id/{id}")
    public ResponseEntity<BibleVerse> getBibleVerseById(@PathVariable Long id) {
        return bibleVerseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get_all_bible_verses")
    public ResponseEntity<List<BibleVerse>> getAllBibleVerses() {
        List<BibleVerse> bibleVerses = bibleVerseRepository.findAll();
        return ResponseEntity.ok(bibleVerses);
    }

    @Transactional
    @PostMapping("/create_bible_verse")
    public ResponseEntity<BibleVerse> createBibleVerse(@RequestBody BibleVerseCreationDTO dto) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        BibleVerse bibleVerse = dto.toBibleVerse();
        logger.debug("Saving BibleVerse: {}", bibleVerse);
        bibleVerseRepository.save(bibleVerse);
        logger.debug("BibleVerse saved successfully");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create_devotional")
    public ResponseEntity<Void> createDevotional(@RequestBody BibleVerse bibleVerse) {
        try {
            Devotional devotional = devotionalService.createDailyDevotional(bibleVerse);
            if (devotional == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
