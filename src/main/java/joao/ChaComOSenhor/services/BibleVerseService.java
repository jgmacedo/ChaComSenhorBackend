package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerseCreationDTO;
import joao.ChaComOSenhor.repositories.BibleVerseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BibleVerseService {

    @Autowired
    private BibleVerseRepository bibleVerseRepository;

    /**
     * Save a new Bible verse
     */
    public BibleVerse createBibleVerse(BibleVerseCreationDTO dto) {
       BibleVerse bibleVerse = dto.toBibleVerse();
       return saveBibleVerse(bibleVerse);
    }

    public BibleVerse saveBibleVerse(BibleVerse bibleVerse) {
        return bibleVerseRepository.save(bibleVerse);
    }
}