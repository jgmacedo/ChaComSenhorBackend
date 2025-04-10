package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.devotional.ApiResponseDTO;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.domain.devotional.DevotionalResponseDTO;
import joao.ChaComOSenhor.services.DevotionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/devotionals")
@PreAuthorize("hasRole('USER')")
public class DevotionalController {
    DevotionalService devotionalService;

    @GetMapping("/today")
    public ResponseEntity<ApiResponseDTO<DevotionalResponseDTO>> getTodaysDevotional() {
        try {
            Devotional devotional = devotionalService.getTodaysDevotional();
            DevotionalResponseDTO dto = new DevotionalResponseDTO(
                devotional.getId(),
                devotional.getTitle(),
                devotional.getReflection(),
                devotional.getPrayer(),
                devotional.getPracticalApplication(),
                devotional.getSupportingVerses(),
                devotional.getDate(),
                devotional.getBibleVerse()
            );
            return ResponseEntity.ok(ApiResponseDTO.success(dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/check_date")
    public ResponseEntity<ApiResponseDTO<DevotionalResponseDTO>> getDateDevotional(@RequestParam LocalDate date) {
        try {
            log.debug("Received date parameter: {}", date);
            boolean exists = devotionalService.checkIfDevotionalExistsForDate(date);
            log.debug("Devotional exists for date {}: {}", date, exists);

            if (!exists) {
                return ResponseEntity.ok(ApiResponseDTO.success(null));
            }

            Devotional devotional = devotionalService.getDevotionalByDate(date);
            DevotionalResponseDTO response = new DevotionalResponseDTO(
                devotional.getId(),
                devotional.getTitle(),
                devotional.getReflection(),
                devotional.getPrayer(),
                devotional.getPracticalApplication(),
                devotional.getSupportingVerses(),
                devotional.getDate(),
                devotional.getBibleVerse()
            );
            return ResponseEntity.ok(ApiResponseDTO.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    @GetMapping("/dates")
    public ResponseEntity<ApiResponseDTO<List<LocalDate>>> getAllDevotionalDates() {
        try {
            List<LocalDate> dates = devotionalService.getAllDevotionalDates();
            return ResponseEntity.ok(ApiResponseDTO.success(dates));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }
}
