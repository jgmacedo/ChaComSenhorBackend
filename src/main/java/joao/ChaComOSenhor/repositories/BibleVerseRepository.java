package joao.ChaComOSenhor.repositories;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BibleVerseRepository extends JpaRepository<BibleVerse, Long> { }
