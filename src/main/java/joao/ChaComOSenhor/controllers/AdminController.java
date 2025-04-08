package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerseCreationDTO;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.UserRepository;
import joao.ChaComOSenhor.services.DevotionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import joao.ChaComOSenhor.services.AiService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AiService aiService;

    private final DevotionalService devotionalService;
    private final UserRepository userRepository;
    private final BibleVerseRepository bibleVerseRepository;

    public AdminController(DevotionalService devotionalService,
                           UserRepository userRepository,
                           BibleVerseRepository bibleVerseRepository,
                           AiService aiService) {
        this.devotionalService = devotionalService;
        this.userRepository = userRepository;
        this.bibleVerseRepository = bibleVerseRepository;
        this.aiService = aiService;
    }

    @GetMapping("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("users/{id}")
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
        BibleVerse bibleVerse = dto.toBibleVerse();
        bibleVerseRepository.save(bibleVerse);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create_devotional/{id}")
    public ResponseEntity<Void> createDevotional(@PathVariable Long id) {
        try {
            BibleVerse bibleVerse = bibleVerseRepository.findById(id)
                    .orElse(null);

            if (bibleVerse == null) {
                return ResponseEntity.notFound().build();
            }

            Devotional devotional = devotionalService.createDailyDevotional(bibleVerse);
            if (devotional == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/test_ai_service/{id}")
    public ResponseEntity<Map<String, Object>> testAiService(@PathVariable Long id) {
        Map<String, Object> results = new HashMap<>();

        try {
            BibleVerse bibleVerse = bibleVerseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bible verse not found"));

            // Test 1: Get the generated prompt
            String prompt = aiService.generateFullPrompt(bibleVerse);
            results.put("generatedPrompt", prompt);

            // Test 2: Get raw API response
            String rawResponse = aiService.sendPostToOpenRouter(bibleVerse);
            results.put("rawApiResponse", rawResponse);

            // Test 3: Try to parse the response
            try {
                Devotional devotional = aiService.parseJsonToDevotional(rawResponse);
                results.put("parsedDevotional", devotional);
                results.put("parseSuccess", true);
            } catch (Exception e) {
                results.put("parseError", e.getMessage());
                results.put("parseSuccess", false);
            }

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            results.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(results);
        }
    }
}
