package joao.ChaComOSenhor.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private AiService aiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiService, "openRouterApiKey", "test-api-key");
        ReflectionTestUtils.setField(aiService, "openRouterApiUrl", "https://openrouter.ai/api/v1/chat/completions");
    }

    @Test
    void generateDevotionalTitle_returnsTitle() throws Exception {
        // Create BibleVerse with all required fields
        BibleVerse bibleVerse = new BibleVerse();
        bibleVerse.setBook("John");
        bibleVerse.setChapter(3);
        bibleVerse.setVerse(16);
        bibleVerse.setText("For God so loved the world...");
        bibleVerse.setReference("John 3:16");

        String expectedResponse = "{\"choices\":[{\"message\":{\"content\":\"Inspiring Title\"}}]}";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String result = aiService.generateDevotionalTitle(bibleVerse);

        assertEquals("Inspiring Title", result);
    }

    @Test
    void generateDevotionalContent_returnsContent() throws Exception {
        // Create BibleVerse with all required fields
        BibleVerse bibleVerse = new BibleVerse();
        bibleVerse.setBook("John");
        bibleVerse.setChapter(3);
        bibleVerse.setVerse(16);
        bibleVerse.setText("For God so loved the world...");
        bibleVerse.setReference("John 3:16");

        String title = "Inspiring Title";
        String expectedResponse = "{\"choices\":[{\"message\":{\"content\":\"Devotional Content\"}}]}";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        String result = aiService.generateDevotionalContent(bibleVerse, title);

        assertEquals("Devotional Content", result);
    }

    @Test
    void callOpenRouterApi_handlesErrorGracefully() throws Exception {
        String prompt = "Test prompt";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("API error"));

        String result = ReflectionTestUtils.invokeMethod(aiService, "callOpenRouterApi", prompt);

        assertEquals("Error calling AI API: API error", result);
    }
}