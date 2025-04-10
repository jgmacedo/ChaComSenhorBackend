package joao.ChaComOSenhor.repositories;

import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Devotional entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface DevotionalRepository extends JpaRepository<Devotional, Long> {

    /**
     * Finds a Devotional by its date.
     *
     * @param date the date of the devotional
     * @return an Optional containing the found Devotional, or empty if not found
     */
    @Query("SELECT d FROM Devotional d WHERE d.date = :date")
    Optional<Devotional> findByDate(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT d.date FROM Devotional d")
    List<LocalDate> findAllDates();
}