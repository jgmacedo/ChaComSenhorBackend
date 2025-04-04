package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerseCreationDTO;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import joao.ChaComOSenhor.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/admin")
public class AdminController {

    private UserRepository userRepository;

    private BibleVerseRepository bibleVerseRepository;

    @GetMapping("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create_bible_verse")
    public ResponseEntity<BibleVerse> createBibleVerse(@RequestBody BibleVerseCreationDTO dto) {
        BibleVerse bibleVerse = dto.toBibleVerse();
        bibleVerseRepository.save(bibleVerse);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/create_devotional")
    public ResponseEntity<Void> createDevotional(@RequestBody BibleVerse bibleVerse) {

        return null;
    }
}
