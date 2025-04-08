package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiServiceTest {

    @Autowired
    private AiService aiService;

    BibleVerse bibleVerse = new BibleVerse(
            "John 3:16",
            "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life"
    );

    @Test
    void generateFullPrompt() {
        String prompt = aiService.generateFullPrompt(bibleVerse);
        assertNotNull(prompt);
        assertTrue(prompt.contains("John 3:16"));
        assertTrue(prompt.contains("For God so loved the world"));
    }

    @Test
    void sendPostToOpenRouter() {
        String response = aiService.sendPostToOpenRouter(bibleVerse);
        assertNotNull(response);
        assertFalse(response.isEmpty());
        System.out.println(response);
    }

    @Test
    void parseJsonToDevotional() {
        // Exemplo de uso:
        String mockJson = """
            {
              "choices": [{
                "message": {
                  "content": "{
                    \\"title\\":\\"God’s Love for the World\\",
                    \\"exactQuote\\":\\"For God so loved the world...\\",
                    \\"reflection\\":\\"This verse reminds us...\\",
                    \\"prayer\\":\\"Lord, help me...\\",
                    \\"practicalApplication\\":\\"Share God's love...\\",
                    \\"supportingVerses\\":\\"Romans 5:8, 1 John 4:9\\"
                  }"
                }
              }]
            }
        """;
        var devotional = aiService.parseJsonToDevotional(mockJson);
        assertNotNull(devotional);
        assertNotNull(devotional.getTitle());
        System.out.println(devotional);
    }
}
