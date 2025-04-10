package joao.ChaComOSenhor.controllers;

    import joao.ChaComOSenhor.domain.devotional.Devotional;
    import joao.ChaComOSenhor.infra.security.TokenService;
    import joao.ChaComOSenhor.services.DevotionalService;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;

    import java.time.LocalDate;
    import java.util.Arrays;
    import java.util.List;

    import static org.mockito.Mockito.when;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @WebMvcTest(DevotionalController.class)
    class DevotionalControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DevotionalService devotionalService;

        @MockBean
        private TokenService tokenService;

        @Test
        void testGetTodaysDevotional() throws Exception {
            Devotional devotional = new Devotional(); // Mock Devotional object
            when(devotionalService.getTodaysDevotional()).thenReturn(devotional);

            mockMvc.perform(get("/devotionals/today"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void testGetCheckDateDevotional_DevotionalExists() throws Exception {
            LocalDate date = LocalDate.of(2023, 10, 1);
            when(devotionalService.checkIfDevotionalExistsForDate(date)).thenReturn(true);

            mockMvc.perform(get("/devotionals/check_date")
                            .param("date", date.toString()))
                    .andExpect(status().isNoContent());
        }

        @Test
        void testGetCheckDateDevotional_DevotionalDoesNotExist() throws Exception {
            LocalDate date = LocalDate.of(2023, 10, 1);
            Devotional devotional = new Devotional(); // Mock Devotional object
            when(devotionalService.checkIfDevotionalExistsForDate(date)).thenReturn(false);
            when(devotionalService.getDevotionalByDate(date)).thenReturn(devotional);

            mockMvc.perform(get("/devotionals/check_date")
                            .param("date", date.toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void testGetAllDevotionalDates() throws Exception {
            List<LocalDate> dates = Arrays.asList(LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 2));
            when(devotionalService.getAllDevotionalDates()).thenReturn(dates);

            mockMvc.perform(get("/devotionals/dates"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }