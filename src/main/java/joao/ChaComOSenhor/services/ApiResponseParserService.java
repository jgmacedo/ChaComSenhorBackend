package joao.ChaComOSenhor.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ApiResponseParserService {

    private final ObjectMapper objectMapper;

    public ApiResponseParserService() {
        this.objectMapper = new ObjectMapper();
    }

    public Devotional parseDevotionalFromApiResponse(String rawApiResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawApiResponse);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");

            if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
                throw new RuntimeException("Content field is missing or empty in the API response.");
            }

            String cleanedContent = cleanJsonContent(contentNode.asText());
            JsonNode contentJson = objectMapper.readTree(cleanedContent);

            Devotional devotional = new Devotional();
            devotional.setTitle(contentJson.path("title").asText());
            devotional.setReflection(contentJson.path("reflection").asText());
            devotional.setPrayer(contentJson.path("prayer").asText());
            devotional.setPracticalApplication(contentJson.path("practicalApplication").asText());
            devotional.setSupportingVerses(contentJson.path("supportingVerses").asText());

            devotional.setDate(LocalDate.now());

            // Create and set main BibleVerse
//            if (contentJson.has("supportingVerses")) {
//                JsonNode firstVerse = contentJson.get("supportingVerses").get(0);
//                BibleVerse bibleVerse = new BibleVerse(
//                        firstVerse.path("reference").asText(),
//                        firstVerse.path("text").asText()
//                );
//                devotional.setBibleVerse(bibleVerse);
//            }

            return devotional;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse devotional content: " + e.getMessage(), e);
        }
    }

    private String cleanJsonContent(String content) {
        return content.replaceAll("```(json)?", "").trim();
    }
}