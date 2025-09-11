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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatService statService;

    @Test
    void testViewStatsAddAttributesToModel() throws Exception {
        StatService mockService = mock(StatService.class);
        StatController controller = new StatController(mockService);

        StatWrapper wrapper = new StatWrapper();
        wrapper.getCurrentStats().add(new Stat("Test Stat", 0));
        wrapper.getArchivedStats().add(new Stat("Archived Stat", 0));
        when(mockService.getStatWrapper()).thenReturn(wrapper);

        Model mockModel = mock(Model.class);
        String viewName = controller.viewStats(mockModel);

        assertEquals("stats", viewName); // the Thymeleaf view name
        verify(mockModel).addAttribute("stats", wrapper.getCurrentStats());
        verify(mockModel).addAttribute("archive", wrapper.getArchivedStats());
    }

    @Test
    void testAddStatCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/add").param("statName", "New Stat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).addNewStat("New Stat");
    }

    @Test
    void testIncrementByOneCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/increment/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).incrementStatByOne(1);
    }

    @Test
    void testIncrementByCustomCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/addCustomAmount").param("index", "1").param("count", "36"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).addCustomAmountToStatAtIndex(1, 36);
    }

    @Test
    void testArchiveStatCorrectlyInvokesService() throws Exception {
        mockMvc.perform(post("/archive").param("index", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(statService).archiveStatAtIndex(1);
    }
}