package joao.ChaComOSenhor.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ApiResponseParserService apiResponseParserService;
    private final String openRouterApiKey;
    private final String openRouterUrl;

    public AiService(ApiResponseParserService apiResponseParserService,
                     @Value("${openrouter.api.key}") String openRouterApiKey,
                     @Value("${openrouter.api.url}") String openRouterUrl) {
        this.apiResponseParserService = apiResponseParserService;
        this.openRouterApiKey = openRouterApiKey;
        this.openRouterUrl = openRouterUrl;
    }

    public String generateFullPrompt(BibleVerse bibleVerse) {
        return String.format("""
                {
                  "model": "meta-llama/llama-4-scout:free",
                  "messages": [
                    {
                      "content": "you are a devotional creator, graduated from a presbyterian seminary",
                      "role": "system"
                    },
                    {
                      "content": "Given the following verse:  Reference: %s Text: %s  Generate the following JSON: {   \\"title\\": \\"\\",   \\"reflection\\": \\"\\",   \\"prayer\\": \\"\\",   \\"practicalApplication\\": \\"\\",   \\"supportingVerses\\": \\"\\" }  Guidelines: - Source Quality: Use well-regarded Christian authors and Scripture (ESV or NIV). - Validation: Outputs must align with trusted theological sources. - Ethical Alignment: Avoid controversial or fringe interpretations. Stick to widely accepted Christian theology. - Avoid denominational bias and ensure doctrinal alignment with historic Christianity. - Do not include any content before or after the JSON.",
                      "role": "user"
                    }
                  ],
                  "response_format": {
                    "type": "json_object"
                  }
                }
                """, bibleVerse.getReference(), bibleVerse.getText());
    }

    public Devotional generateDevotional(BibleVerse bibleVerse) {
        try {
            validateBibleVerse(bibleVerse);

            String jsonPayload = generateFullPrompt(bibleVerse);
            String apiResponse = sendPostToOpenRouter(jsonPayload);

            return apiResponseParserService.parseDevotionalFromApiResponse(apiResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error generating devotional: " + e.getMessage(), e);
        }
    }

    private String sendPostToOpenRouter(String jsonPayload) {
        try {
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(openRouterUrl))
                    .header("Authorization", "Bearer " + openRouterApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to get valid response. Status: " + response.statusCode());
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Error while requesting devotional: " + e.getMessage(), e);
        }
    }

    private void validateBibleVerse(BibleVerse bibleVerse) {
        if (bibleVerse == null || bibleVerse.getReference() == null || bibleVerse.getText() == null) {
            throw new IllegalArgumentException("BibleVerse or its properties are null");
        }
    }
}