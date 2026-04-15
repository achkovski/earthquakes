package com.kirilachkovski.earthquakes.controller;

import com.kirilachkovski.earthquakes.service.EarthquakeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.kirilachkovski.earthquakes.factory.EarthquakeTestFactory.sampleDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EarthquakeController.class)
class EarthquakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EarthquakeService earthquakeService;

    @Test
    void getAll_returns200WithList_whenNoFiltersProvided() throws Exception {
        when(earthquakeService.getAll(Optional.empty(), Optional.empty()))
                .thenReturn(List.of(sampleDto("ext-1")));

        mockMvc.perform(get("/api/earthquakes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].externalId").value("ext-1"))
                .andExpect(jsonPath("$[0].magnitude").value(4.5));
    }

    @Test
    void getAll_passesParsedFiltersToService_whenAfterAndMinMagProvided() throws Exception {
        when(earthquakeService.getAll(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/earthquakes")
                        .param("after", "2024-01-15")
                        .param("minMag", "4.5"))
                .andExpect(status().isOk());

        ArgumentCaptor<Optional<Instant>> afterCaptor = ArgumentCaptor.forClass(Optional.class);
        ArgumentCaptor<Optional<BigDecimal>> minMagCaptor = ArgumentCaptor.forClass(Optional.class);
        verify(earthquakeService).getAll(afterCaptor.capture(), minMagCaptor.capture());

        Instant expectedInstant = LocalDate.of(2024, 1, 15)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        assertThat(afterCaptor.getValue()).contains(expectedInstant);
        assertThat(minMagCaptor.getValue().get()).isEqualByComparingTo("4.5");
    }

    @Test
    void getById_returns200WithDto_whenFound() throws Exception {
        when(earthquakeService.getById(1L)).thenReturn(sampleDto("ext-1"));

        mockMvc.perform(get("/api/earthquakes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("ext-1"));
    }

    @Test
    void getById_returns404_whenServiceThrowsEntityNotFound() throws Exception {
        when(earthquakeService.getById(99L))
                .thenThrow(new EntityNotFoundException("Earthquake data not found for id: 99"));

        mockMvc.perform(get("/api/earthquakes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("99")));
    }

    @Test
    void getById_returns400_whenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/earthquakes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("abc")));
    }

    @Test
    void deleteById_returns204_whenSuccess() throws Exception {
        mockMvc.perform(delete("/api/earthquakes/1"))
                .andExpect(status().isNoContent());

        verify(earthquakeService).deleteById(1L);
    }

    @Test
    void deleteById_returns404_whenServiceThrowsEntityNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Earthquake data not found for id: 99"))
                .when(earthquakeService).deleteById(99L);

        mockMvc.perform(delete("/api/earthquakes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("99")));
    }

    @Test
    void refresh_returns200WithSavedCount() throws Exception {
        when(earthquakeService.refreshFromUsgs()).thenReturn(7);

        mockMvc.perform(post("/api/earthquakes/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Saved").value(7));
    }
}
