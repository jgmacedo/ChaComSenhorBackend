package joao.ChaComOSenhor.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AiService {
    private static final Logger logger = Logger.getLogger(AiService.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openrouter.api.key:${OPENROUTER_API_KEY}}")
    private String openRouterApiKey;

    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String openRouterApiUrl;

    private final RestTemplate restTemplate;

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateDevotionalTitle(BibleVerse bibleVerse) {
        String prompt = String.format(
                "Generate a short, inspiring title for a devotional based on %s which says: '%s'",
                bibleVerse.getReference(),
                bibleVerse.getText()
        );

        return callOpenRouterApi(prompt);
    }

    public String generateDevotionalContent(BibleVerse bibleVerse, String title) {
        String prompt = String.format(
                "Write a devotional reflection (300-500 words) with the title '%s' based on the Bible verse %s which says: '%s'. " +
                        "The content should directly relate to the title theme. " +
                        "Include spiritual insights, practical application, and a prayer at the end.",
                title,
                bibleVerse.getReference(),
                bibleVerse.getText()
        );

        return callOpenRouterApi(prompt);
    }

    private String callOpenRouterApi(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // Create the message structure
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            // Create the request payload
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "openai/gpt-3.5-turbo");
            requestMap.put("messages", new Object[]{message});

            // Convert map to JSON string
            String requestBody = objectMapper.writeValueAsString(requestMap);

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(openRouterApiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openRouterApiKey)
                    .header("HTTP-Referer", "https://yourapp.com") // Replace with your site URL
                    .header("X-Title", "Your App") // Replace with your app name
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the response to extract the content
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            return jsonResponse
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI API: " + e.getMessage();
        }

    }
}