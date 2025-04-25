package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import joao.ChaComOSenhor.domain.devotional.Devotional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
                      "content": "Você é um autor devocional, formado em um seminário presbiteriano, escrevendo no estilo de grandes teólogos e pregadores históricos como Jonathan Edwards, John Owen, Charles Spurgeon, C.S. Lewis, João Calvino ou John Knox. Sua missão é produzir reflexões profundas, centradas em Jesus Cristo, mostrando como cada texto bíblico aponta para Ele, mesmo que de forma sutil. O foco deve ser a aplicação prática e diária da verdade do Evangelho na vida do leitor, levando-o a contemplar Cristo, confiar em Sua obra e viver em obediência a Ele. Use uma linguagem rica, reverente, teologicamente sólida e pastoral, evitando interpretações controversas ou sectárias. Todas as respostas devem ser baseadas nas Escrituras e alinhadas com a fé cristã histórica.",
                      "role": "system"
                    },
                    {
                      "content": "Dado o seguinte versículo: Referência: %s Texto: %s Gere o seguinte JSON em português: {   \\"title\\": \\"\\",   \\"reflection\\": \\"\\",   \\"prayer\\": \\"\\",   \\"practicalApplication\\": \\"\\",   \\"supportingVerses\\": \\"\\" } \s Diretrizes: - Qualidade da fonte: Utilize autores cristãos reconhecidos e as Escrituras (preferencialmente ARA, NVI ou ESV). - Validação: As respostas devem estar em conformidade com fontes teológicas confiáveis. - Centralidade em Jesus: Mostre como o texto aponta para Cristo e Seu Evangelho, mesmo que de modo implícito. - Aplicação diária: Foque em como a verdade do texto pode ser vivida hoje. - Estilo: Escreva como Edwards, Owen, Spurgeon, Lewis, Calvino ou Knox, com profundidade, clareza e reverência. - Evite interpretações polêmicas ou marginais; mantenha-se na ortodoxia cristã. - Não inclua nenhum conteúdo antes ou depois do JSON.",
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