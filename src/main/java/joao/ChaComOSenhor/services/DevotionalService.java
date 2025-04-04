package joao.ChaComOSenhor.services;

            import com.fasterxml.jackson.databind.JsonNode;
            import com.fasterxml.jackson.databind.ObjectMapper;
            import joao.ChaComOSenhor.domain.bible_verse.BibleVerse;
            import joao.ChaComOSenhor.domain.devotional.Devotional;
            import joao.ChaComOSenhor.repositories.DevotionalRepository;
            import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.stereotype.Service;

            import java.time.LocalDate;
            import java.util.Arrays;
            import java.util.List;
            import java.util.logging.Logger;

            @Service
            public class DevotionalService {
                private static final Logger logger = Logger.getLogger(DevotionalService.class.getName());

                @Autowired
                private DevotionalRepository devotionalRepository;

                @Autowired
                private AiService aiService;

                public Devotional createDailyDevotional(BibleVerse bibleVerse) {
                    try {
                        LocalDate today = LocalDate.now();

                        // Check if a devotional already exists for today
                        if (devotionalRepository.findByDate(today).isPresent()) {
                            throw new RuntimeException("A devotional for today already exists");
                        }

                        Devotional devotional = new Devotional();
                        devotional.setDate(today);
                        devotional.setBibleVerse(bibleVerse);

                        // First generate title
                        String title = aiService.generateDevotionalTitle(bibleVerse);
                        devotional.setTitle(title);

                        // Then pass title to content generation
                        String content = aiService.generateDevotionalContent(bibleVerse, title);

                        // Parse JSON content and set fields accordingly
                        parseAndSetDevotionalContent(devotional, content);

                        devotionalRepository.save(devotional);
                        logger.info("Devocional diária criada com sucesso para " + today);

                        return devotional;
                    } catch (Exception e) {
                        logger.severe("Erro ao criar devocional diária: " + e.getMessage());
                        throw new RuntimeException("Failed to create daily devotional: " + e.getMessage(), e);
                    }
                }

                private void parseAndSetDevotionalContent(Devotional devotional, String content) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(content);

                        // Extract each field from the JSON content
                        if (rootNode.has("reflection")) {
                            devotional.setReflection(rootNode.get("reflection").asText());
                        }

                        if (rootNode.has("prayer")) {
                            devotional.setPrayer(rootNode.get("prayer").asText());
                        }

                        if (rootNode.has("practicalApplication") && rootNode.get("practicalApplication").has("step")) {
                            devotional.setPracticalApplication(rootNode.get("practicalApplication").get("step").asText());
                        }

                        if (rootNode.has("validation")) {
                            JsonNode validationNode = rootNode.get("validation");

                            if (validationNode.has("sources")) {
                                StringBuilder sourcesBuilder = new StringBuilder();
                                validationNode.get("sources").forEach(source ->
                                    sourcesBuilder.append(source.asText()).append("\n"));
                                devotional.setValidationSources(sourcesBuilder.toString().trim());
                            }

                            if (validationNode.has("ethicalAlignment")) {
                                devotional.setEthicalAlignment(validationNode.get("ethicalAlignment").asText());
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("Error parsing devotional content: " + e.getMessage());
                        // Set raw content as reflection if parsing fails
                        devotional.setReflection(content);
                    }
                }

                public Devotional getTodaysDevotional(){
                    LocalDate today = LocalDate.now();
                    return devotionalRepository.findByDate(today)
                            .orElseThrow(() -> new RuntimeException("Não encontramos a devocional de hoje."));
                }

                public Devotional getDevotionalByDate(LocalDate date){
                    return devotionalRepository.findByDate(date)
                            .orElseThrow(() -> new RuntimeException("Não encontramos a devocional do dia " + date.toString()));
                }
            }