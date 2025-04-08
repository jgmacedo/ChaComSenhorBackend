package joao.ChaComOSenhor.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String openRouterApiKey;
    private final String openRouterUrl;

    public AiService(@Value("${openrouter.api.key}") String openRouterApiKey,
                     @Value("${openrouter.api.url}") String openRouterUrl) {
        this.openRouterApiKey = openRouterApiKey;
        this.openRouterUrl = openRouterUrl;
    }

    public String generateFullPrompt(BibleVerse bibleVerse) {
        return String.format("""
        ANSWER ONLY THE JSON, NOTHING ELSE. Do not include formatting or explanations.
        You are a theologian and devotional writer. Given the following verse:

        Reference: %s
        Text: %s

        Generate the following JSON:
        {
          "exactQuote": "",
          "title": "",
          "reflection": "",
          "prayer": "",
          "practicalApplication": "",
          "supportingVerses": ""
        }

        Guidelines:
        - Source Quality: Use well-regarded Christian authors and Scripture (ESV or NIV).
        - Validation: Outputs must align with trusted theological sources.
        - Ethical Alignment: Avoid controversial or fringe interpretations. Stick to widely accepted Christian theology.
        - Avoid denominational bias and ensure doctrinal alignment with historic Christianity.
        - Do not include any content before or after the JSON.
        """, bibleVerse.getReference(), bibleVerse.getText());
    }

    public String sendPostToOpenRouter(BibleVerse bibleVerse) {
        try {
            if (bibleVerse == null || bibleVerse.getReference() == null || bibleVerse.getText() == null) {
                throw new IllegalArgumentException("BibleVerse ou propriedades nulas");
            }

            var client = HttpClient.newHttpClient();

            // Construção do prompt
            String prompt = generateFullPrompt(bibleVerse);

            // Construção do corpo da requisição conforme a API da OpenRouter
            Map<String, Object> requestBody = Map.of(
                    "model", "deepseek/deepseek-r1-distill-llama-70b:free",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", prompt
                    ))
            );

            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(openRouterUrl))
                    .header("Authorization", "Bearer " + openRouterApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao solicitar a devocional: " + e.getMessage(), e);
        }
    }


    public Devotional parseJsonToDevotional(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            try {
                // Primeira tentativa: parse completo da resposta
                root = mapper.readTree(jsonResponse);
            } catch (Exception ex) {
                // Fallback: tenta extrair apenas o JSON bruto usando regex
                String jsonExtracted = extractJsonFromString(jsonResponse);
                if (jsonExtracted == null) {
                    throw new RuntimeException("Resposta da IA não contém JSON válido ou extraível.");
                }
                root = mapper.readTree(jsonExtracted);
            }

            // Detecta se está dentro de estrutura como: {"choices": [{"message": {"content": "{json}"}}]}
            if (root.has("choices")) {
                String content = root.path("choices").get(0).path("message").path("content").asText();
                root = mapper.readTree(content);  // Parseia o JSON que veio como string
            }

            // Verifica se todos os campos necessários existem e não são nulos
            for (String field : List.of("title", "exactQuote", "reflection", "prayer", "practicalApplication", "supportingVerses")) {
                if (!root.has(field) || root.get(field).asText().isBlank()) {
                    throw new RuntimeException("Campo obrigatório faltando ou vazio: " + field);
                }
            }

            return mapper.treeToValue(root, Devotional.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta da IA: " + e.getMessage(), e);
        }
    }

    private String extractJsonFromString(String input) {
        if (input == null || input.isBlank()) return null;

        // Using a simpler approach to find JSON objects
        Pattern pattern = Pattern.compile("\\{[^{}]*((\\{[^{}]*})[^{}]*)*}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String candidate = matcher.group();
            // Verifica se parece com um JSON válido (pelo menos contém uma chave e valor)
            if (candidate.contains(":")) {
                return candidate;
            }
        }

        return null;
    }

}
