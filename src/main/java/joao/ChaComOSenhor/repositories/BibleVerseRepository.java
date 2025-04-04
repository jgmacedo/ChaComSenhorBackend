package joao.ChaComOSenhor.repositories;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BibleVerseRepository extends JpaRepository<BibleVerse, Long> { }
