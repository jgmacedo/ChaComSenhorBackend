package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BibleVerseService {

    @Autowired
    private BibleVerseRepository bibleVerseRepository;

    /**
     * Save a new Bible verse
     */
    public BibleVerse saveBibleVerse(BibleVerse bibleVerse) {
        // Format reference if not already formatted
        if (bibleVerse.getReference() == null || bibleVerse.getReference().isEmpty()) {
            bibleVerse.setReference(formatReference(bibleVerse.getBook(), bibleVerse.getChapter(), bibleVerse.getVerse()));
        }
        return bibleVerseRepository.save(bibleVerse);
    }

    /**
     * Get Bible verse by ID
     */
    public BibleVerse getBibleVerseById(Long id) {
        return (BibleVerse) bibleVerseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Versículo bíblico não encontrado com ID: " + id));
    }

    /**
     * Delete Bible verse
     */
    public void deleteBibleVerse(Long id) {
        bibleVerseRepository.deleteById(id);
    }

    /**
     * Format reference string in the pattern "Book Chapter:Verse"
     */
    private String formatReference(String book, Integer chapter, Integer verse) {
        return book + " " + chapter + ":" + verse;
    }
}