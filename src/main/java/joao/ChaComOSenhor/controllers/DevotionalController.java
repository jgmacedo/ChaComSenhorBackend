package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.services.DevotionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequestMapping("/devotionals")
@PreAuthorize("hasRole('USER')")
public class DevotionalController {
    private final DevotionalService devotionalService;

    public DevotionalController(DevotionalService devotionalService) {
        this.devotionalService = devotionalService;
    }

    @GetMapping("/today")
    public ResponseEntity<Devotional> getTodaysDevotional() {
        Devotional devotional = devotionalService.getTodaysDevotional();
        return ResponseEntity.ok(devotional);
    }

    @GetMapping("/check_date")
    public ResponseEntity<Devotional> getCheckDateDevotional(@RequestParam LocalDate date) {
        try {
            if (devotionalService.checkIfDevotionalExistsForDate(date)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(devotionalService.getDevotionalByDate(date));
        } catch (Exception e) {
            log.error("Error fetching devotional for date {}: {}", date, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/dates")
    public ResponseEntity<List<LocalDate>> getAllDevotionalDates(){
        try{
            List<LocalDate> dates = devotionalService.getAllDevotionalDates();
            return ResponseEntity.ok(dates);
        } catch (Exception e){
            log.error("Error fetching available devotional dates {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
