package com.aden.statsApp.controller;

import com.aden.statsApp.model.Stat;
import com.aden.statsApp.model.StatWrapper;
import com.aden.statsApp.service.StatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatController.class)
class StatControllerTest {

    public static final String MOCK_STAT_NAME = "Mock Stat";
    private static final String STAT_NAME_PARAM = "statName";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatService statService;

    @Test
    void testViewStatsAddAttributesToModel() throws Exception {
        StatService mockService = mock(StatService.class);
        StatController controller = new StatController(mockService);

        StatWrapper wrapper = new StatWrapper();
        wrapper.getCurrentStats().put("Test Stat", new Stat(0));
        wrapper.getArchivedStats().put("Archived Stat", new Stat(0));
        when(mockService.getStatWrapper()).thenReturn(wrapper);

        Model mockModel = mock(Model.class);
        String viewName = controller.viewStats(mockModel);

        assertEquals("stats", viewName); // the Thymeleaf view name
        verify(mockModel).addAttribute("stats", mockService.getSortedCurrentStats());
        verify(mockModel).addAttribute("archive", mockService.getSortedArchivedStats());
    }

    @Test
    void testAddStatCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/add").param(STAT_NAME_PARAM, MOCK_STAT_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).addNewStat(MOCK_STAT_NAME);
    }

    @Test
    void testIncrementByOneCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/increment").param(STAT_NAME_PARAM, MOCK_STAT_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).incrementStatByOne("Mock Stat");
    }

    @Test
    void testIncrementByCustomCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/addCustomAmount").param(STAT_NAME_PARAM, MOCK_STAT_NAME).param("count", "36"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).addCustomAmountToStatByName(MOCK_STAT_NAME, 36);
    }

    @Test
    void testArchiveStatCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/archive").param(STAT_NAME_PARAM, MOCK_STAT_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).archiveStatByName(MOCK_STAT_NAME);
    }
}