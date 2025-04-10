package joao.ChaComOSenhor.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;

@Service
public class ApiResponseParserService {

    private final ObjectMapper objectMapper;

    public ApiResponseParserService() {
        this.objectMapper = new ObjectMapper();
    }

    public Devotional parseDevotionalFromApiResponse(String rawApiResponse) {
        try {
            // Parse the raw API response
            JsonNode root = objectMapper.readTree(rawApiResponse);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");

            // Validate the content node
            if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
                throw new RuntimeException("Content field is missing or empty in the API response.");
            }

            // Clean and parse the content JSON
            String cleanedContent = cleanJsonContent(contentNode.asText());
            JsonNode contentJson = objectMapper.readTree(cleanedContent);

            // Create and populate the Devotional object
            Devotional devotional = new Devotional();
            devotional.setTitle(contentJson.path("title").asText(""));
            devotional.setReflection(contentJson.path("reflection").asText(""));
            devotional.setPrayer(contentJson.path("prayer").asText(""));
            devotional.setPracticalApplication(contentJson.path("practicalApplication").asText(""));

            // Process supportingVerses array
            StringBuilder supportingVersesBuilder = new StringBuilder();
            if (contentJson.has("supportingVerses") && contentJson.get("supportingVerses").isArray()) {
                Iterator<JsonNode> elements = contentJson.withArray("supportingVerses").elements();
                while (elements.hasNext()) {
                    JsonNode node = elements.next();
                    if (node.isTextual()) { // Ensure the node is a string
                        if (!supportingVersesBuilder.isEmpty()) {
                            supportingVersesBuilder.append("\n");
                        }
                        supportingVersesBuilder.append(node.asText());
                    }
                }
            }
            devotional.setSupportingVerses(supportingVersesBuilder.toString());
            devotional.setDate(LocalDate.now());

            // Create and set the main BibleVerse
            if (contentJson.has("supportingVerses") && contentJson.get("supportingVerses").isArray()) {
                JsonNode firstVerse = contentJson.get("supportingVerses").get(0);
                if (firstVerse != null && firstVerse.has("reference") && firstVerse.has("text")) {
                    BibleVerse bibleVerse = new BibleVerse(
                            firstVerse.path("reference").asText(""),
                            firstVerse.path("text").asText("")
                    );
                    devotional.setBibleVerse(bibleVerse);
                }
            }

            return devotional;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse devotional content: " + e.getMessage(), e);
        }
    }

    private String cleanJsonContent(String content) {
        return content.replaceAll("```(json)?", "").trim();
    }
}