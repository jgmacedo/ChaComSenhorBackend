package joao.ChaComOSenhor.controllers;

import jakarta.persistence.EntityNotFoundException;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerseCreationDTO;
import joao.ChaComOSenhor.domain.devotional.ApiResponseDTO;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.exceptions.ResourceNotFoundException;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import joao.ChaComOSenhor.repositories.UserRepository;
import joao.ChaComOSenhor.services.DevotionalService;
import joao.ChaComOSenhor.services.ResponseBuilderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
    private final ResponseBuilderService responseBuilderService;
    private final DevotionalRepository devotionalRepository;

    public AdminController(DevotionalService devotionalService,
                           UserRepository userRepository,
                           BibleVerseRepository bibleVerseRepository,
                           AiService aiService, ResponseBuilderService responseBuilderService, DevotionalRepository devotionalRepository) {
        this.devotionalService = devotionalService;
        this.userRepository = userRepository;
        this.bibleVerseRepository = bibleVerseRepository;
        this.aiService = aiService;
        this.responseBuilderService = responseBuilderService;
        this.devotionalRepository = devotionalRepository;
    }

    @GetMapping("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
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
    @DeleteMapping("/delete_bible_verse/{id}")
    public ResponseEntity<String> deleteBibleVerseById(@PathVariable Long id) {
        if (bibleVerseRepository.existsById(id)) {
            bibleVerseRepository.deleteById(id);
            return ResponseEntity.ok("Bible verse deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bible verse not found.");
        }
    }

    @Transactional
    @PostMapping("/create_bible_verse")
    public ResponseEntity<BibleVerse> createBibleVerse(@RequestBody BibleVerseCreationDTO dto) {
        BibleVerse bibleVerse = dto.toBibleVerse();
        bibleVerseRepository.save(bibleVerse);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @GetMapping("/create_daily_devotional/{id}")
    public ResponseEntity<ApiResponseDTO<?>> testAiService(@PathVariable Long id) {
        try {
            // Generate the devotional
            Devotional devotional = devotionalService.generateCompleteDevotional(id);

            // Build the response
            Map<String, Object> response = responseBuilderService.buildDevotionalResponse(devotional);
            devotionalService.saveDevotional(devotional);
            return ResponseEntity.ok(ApiResponseDTO.success(response));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Bible verse not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Incomplete devotional: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error generating devotional"));
        }
    }

    @Transactional
    @DeleteMapping("/delete_devotional/{id}")
    ResponseEntity<String> deleteDevotional(@PathVariable Long id) {
        try {
            if (!devotionalRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Devotional not found");
            }
            devotionalRepository.deleteById(id);
            return ResponseEntity.ok("Devotional deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Devotional not found");
        }
    }
}
