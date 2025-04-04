package joao.ChaComOSenhor.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${openrouter.api.url}")
    private String openRouterUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String generateFullPrompt(BibleVerse bibleVerse){
        return String.format(
                """     
                        ANSWER ONLY THE JSON, NOTHING ELSE. Do not include formatting or explanations. You are a theologian and devotional writer. Given %s and %s, generate: An exact quote of the verse (ESV translation). A title reflecting the verse's theme. A 150-word reflection connecting the verse to daily Christian life. A short prayer based on the verse. A practical application step. Prioritize these 3 concepts: Source Quality: Prioritize well-regarded Christian authors and Scripture (ESV or NIV) to ensure doctrinal soundness 10. Validation: Add a review step (automated) to cross-check outputs against trusted theological resources 10. Ethical Alignment: Avoid controversial interpretations by restricting training data to widely accepted texts Avoid denominational bias and ensure doctrinal alignment with historic Christianity. Structure it exactly like this json template:
                        {
                          "exactQuote":
                          "title":
                          "reflection":
                          "prayer":
                          "practicalApplication":
                          "supportingVerses":
                        }
                        """,
                bibleVerse.getReference(), bibleVerse.getText()
        );
    }

    public String sendPostToOpenRouter(BibleVerse bibleVerse) {
    try {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(openRouterUrl))
                .header("Authorization", "Bearer " + this.openRouterApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(generateFullPrompt(bibleVerse)))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao solicitar a devocional: " + e.getMessage(), e);
        }
    }

    public Devotional parseJsonToDevotional(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, Devotional.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao parsear o JSON: " + e.getMessage(), e);
        }
    }
}