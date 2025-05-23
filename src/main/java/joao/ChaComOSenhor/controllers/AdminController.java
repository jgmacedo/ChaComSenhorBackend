package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerseCreationDTO;
import joao.ChaComOSenhor.domain.devotional.ApiResponseDTO;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.domain.devotional.DevotionalCreatorDTO;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.exceptions.ResourceNotFoundException;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import joao.ChaComOSenhor.repositories.UserRepository;
import joao.ChaComOSenhor.services.DevotionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DevotionalService devotionalService;
    private final UserRepository userRepository;
    private final BibleVerseRepository bibleVerseRepository;
    private final DevotionalRepository devotionalRepository;

    public AdminController(DevotionalService devotionalService,
                           UserRepository userRepository,
                           BibleVerseRepository bibleVerseRepository,
                           DevotionalRepository devotionalRepository) {
        this.devotionalService = devotionalService;
        this.userRepository = userRepository;
        this.bibleVerseRepository = bibleVerseRepository;
        this.devotionalRepository = devotionalRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponseDTO<List<User>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(ApiResponseDTO.success(users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @Transactional
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponseDTO.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/get_bible_verse_by_id/{id}")
    public ResponseEntity<ApiResponseDTO<BibleVerse>> getBibleVerseById(@PathVariable Long id) {
        try {
            return bibleVerseRepository.findById(id)
                    .map(verse -> ResponseEntity.ok(ApiResponseDTO.success(verse)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponseDTO.error("Bible verse not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/get_all_bible_verses")
    public ResponseEntity<ApiResponseDTO<List<BibleVerse>>> getAllBibleVerses() {
        try {
            List<BibleVerse> bibleVerses = bibleVerseRepository.findAll();
            return ResponseEntity.ok(ApiResponseDTO.success(bibleVerses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/get_all_devotionals")
    public ResponseEntity<ApiResponseDTO<List<Devotional>>> getAllDevotionals() {
        try {
            List<Devotional> devotionals = devotionalRepository.findAll();
            return ResponseEntity.ok(ApiResponseDTO.success(devotionals));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @Transactional
    @DeleteMapping("/delete_bible_verse/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteBibleVerseById(@PathVariable Long id) {
        try {
            if (!bibleVerseRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Bible verse not found"));
            }
            bibleVerseRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponseDTO.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @Transactional
    @PostMapping("/create_bible_verse")
    public ResponseEntity<ApiResponseDTO<BibleVerse>> createBibleVerse(@RequestBody BibleVerseCreationDTO dto) {
        try {
            BibleVerse bibleVerse = dto.toBibleVerse();
            bibleVerseRepository.save(bibleVerse);
            return ResponseEntity.ok(ApiResponseDTO.success(bibleVerse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @Transactional
    @PostMapping("/create_devotional")
    public ResponseEntity<ApiResponseDTO<DevotionalCreatorDTO>> createDevotional(
            @RequestHeader("verse-id") Long verseId,
            @RequestHeader("devotional-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Devotional devotional = devotionalService.generateCompleteDevotional(verseId, date);
            Devotional savedDevotional = devotionalService.saveDevotional(devotional);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success(DevotionalCreatorDTO.fromDevotional(savedDevotional)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.error(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Bible verse not found"));
        } catch (Exception e) {
            log.error("Error creating devotional", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error generating devotional"));
        }
    }

    @Transactional
    @DeleteMapping("/delete_devotional/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteDevotional(@PathVariable Long id) {
        try {
            if (!devotionalRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error("Devotional not found"));
            }
            devotionalRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponseDTO.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }
}
