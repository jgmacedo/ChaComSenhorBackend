package joao.ChaComOSenhor.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseBuilderService {
    private final ObjectMapper objectMapper;

    public Map<String, Object> buildDevotionalResponse(Devotional devotional) {
        if (devotional == null) {
            throw new IllegalArgumentException("Devotional cannot be null");
        }

        BibleVerse bibleVerse = devotional.getBibleVerse();
        if (bibleVerse == null) {
            throw new IllegalStateException("Devotional must have an associated BibleVerse");
        }

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("id", devotional.getId());
        response.put("title", devotional.getTitle());
        response.put("reflection", devotional.getReflection());
        response.put("prayer", devotional.getPrayer());
        response.put("practicalApplication", devotional.getPracticalApplication());
        response.put("supportingVerses", devotional.getSupportingVerses());
        response.put("date", devotional.getDate());
        response.put("bibleVerse", buildBibleVerseResponse(bibleVerse));

        return response;
    }

    private Map<String, Object> buildBibleVerseResponse(BibleVerse bibleVerse) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", bibleVerse.getId());
        response.put("text", bibleVerse.getText());
        response.put("reference", bibleVerse.getReference());
        return response;
    }

}