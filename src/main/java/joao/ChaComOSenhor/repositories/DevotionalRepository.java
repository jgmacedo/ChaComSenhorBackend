package joao.ChaComOSenhor.repositories;

import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DevotionalRepository  extends JpaRepository<Devotional, Long> {
    Optional<Devotional> findByDate(LocalDate date);
}
