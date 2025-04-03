package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.devotional.Devotional;
import joao.ChaComOSenhor.repositories.DevotionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DevotionalService {
    @Autowired
    private DevotionalRepository devotionalRepository;

    public Devotional getTodaysDevotional(){
        LocalDate today = LocalDate.now();
        return devotionalRepository.findByDate(today)
                .orElseThrow(() -> new RuntimeException("Não encontramos a devocional de hoje."));
    }
}
