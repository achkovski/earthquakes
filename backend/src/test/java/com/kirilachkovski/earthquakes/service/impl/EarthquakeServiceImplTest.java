package com.kirilachkovski.earthquakes.service.impl;

import com.kirilachkovski.earthquakes.client.UsgsEarthquakeClient;
import com.kirilachkovski.earthquakes.dto.EarthquakeDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsFeatureDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsGeometryDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsPropertiesDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsResponseDto;
import com.kirilachkovski.earthquakes.entity.Earthquake;
import com.kirilachkovski.earthquakes.repository.EarthquakeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kirilachkovski.earthquakes.factory.EarthquakeTestFactory.sampleEntity;
import static com.kirilachkovski.earthquakes.factory.EarthquakeTestFactory.validFeature;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EarthquakeServiceImplTest {

    @Mock
    private EarthquakeRepository earthquakeRepository;

    @Mock
    private UsgsEarthquakeClient usgsEarthquakeClient;

    @InjectMocks
    private EarthquakeServiceImpl service;

    @Captor
    private ArgumentCaptor<List<Earthquake>> earthquakesCaptor;

    @Test
    void getById_returnsDto_whenEntityExists() {
        Earthquake entity = sampleEntity("ext-1");
        when(earthquakeRepository.findById(1L)).thenReturn(Optional.of(entity));

        EarthquakeDto result = service.getById(1L);

        assertThat(result.externalId()).isEqualTo("ext-1");
        assertThat(result.magnitude()).isEqualByComparingTo("4.5");
    }

    @Test
    void getById_throwsEntityNotFound_whenEntityMissing() {
        when(earthquakeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteById_deletesEntity_whenEntityExists() {
        when(earthquakeRepository.existsById(1L)).thenReturn(true);

        service.deleteById(1L);

        verify(earthquakeRepository).deleteById(1L);
    }

    @Test
    void deleteById_throwsAndDoesNotDelete_whenEntityMissing() {
        when(earthquakeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(earthquakeRepository, never()).deleteById(any());
    }

    @Test
    void refreshFromUsgs_savesAllFeatures_whenNoDuplicatesExist() {
        UsgsFeatureDto f1 = validFeature("f1");
        UsgsFeatureDto f2 = validFeature("f2");
        when(usgsEarthquakeClient.fetch()).thenReturn(new UsgsResponseDto(List.of(f1, f2)));
        when(earthquakeRepository.findAllExternalIds()).thenReturn(Set.of());

        int saved = service.refreshFromUsgs();

        assertThat(saved).isEqualTo(2);
        verify(earthquakeRepository).saveAll(earthquakesCaptor.capture());
        assertThat(earthquakesCaptor.getValue())
                .extracting(Earthquake::getExternalId)
                .containsExactly("f1", "f2");
    }

    @Test
    void refreshFromUsgs_skipsDuplicates() {
        UsgsFeatureDto f1 = validFeature("f1");
        UsgsFeatureDto f2 = validFeature("f2");
        when(usgsEarthquakeClient.fetch()).thenReturn(new UsgsResponseDto(List.of(f1, f2)));
        when(earthquakeRepository.findAllExternalIds()).thenReturn(Set.of("f1"));

        int saved = service.refreshFromUsgs();

        assertThat(saved).isEqualTo(1);
        verify(earthquakeRepository).saveAll(earthquakesCaptor.capture());
        assertThat(earthquakesCaptor.getValue())
                .extracting(Earthquake::getExternalId)
                .containsExactly("f2");
    }

    @Test
    void refreshFromUsgs_skipsInvalidFeatures() {
        UsgsFeatureDto valid = validFeature("valid");
        UsgsFeatureDto invalidMissingMag = new UsgsFeatureDto(
                "invalid",
                new UsgsPropertiesDto(null, "somewhere", 1700000000000L, "mb", "title"),
                new UsgsGeometryDto(List.of(new BigDecimal("-120"), new BigDecimal("38")))
        );
        when(usgsEarthquakeClient.fetch())
                .thenReturn(new UsgsResponseDto(List.of(valid, invalidMissingMag)));
        when(earthquakeRepository.findAllExternalIds()).thenReturn(Set.of());

        int saved = service.refreshFromUsgs();

        assertThat(saved).isEqualTo(1);
        verify(earthquakeRepository).saveAll(earthquakesCaptor.capture());
        assertThat(earthquakesCaptor.getValue())
                .extracting(Earthquake::getExternalId)
                .containsExactly("valid");
    }

    @Test
    void refreshFromUsgs_mapsUsgsFieldsToEntityCorrectly() {
        UsgsFeatureDto feature = new UsgsFeatureDto(
                "ext-abc",
                new UsgsPropertiesDto(
                        new BigDecimal("4.5"),
                        "10km N of Test",
                        1700000000000L,
                        "mb",
                        "M 4.5 - 10km N of Test"
                ),
                new UsgsGeometryDto(List.of(
                        new BigDecimal("-120.5"),
                        new BigDecimal("38.2"),
                        new BigDecimal("10.0")
                ))
        );
        when(usgsEarthquakeClient.fetch()).thenReturn(new UsgsResponseDto(List.of(feature)));
        when(earthquakeRepository.findAllExternalIds()).thenReturn(Set.of());

        service.refreshFromUsgs();

        verify(earthquakeRepository).saveAll(earthquakesCaptor.capture());
        Earthquake saved = earthquakesCaptor.getValue().getFirst();
        assertThat(saved.getExternalId()).isEqualTo("ext-abc");
        assertThat(saved.getMagnitude()).isEqualByComparingTo("4.5");
        assertThat(saved.getMagType()).isEqualTo("mb");
        assertThat(saved.getPlace()).isEqualTo("10km N of Test");
        assertThat(saved.getTitle()).isEqualTo("M 4.5 - 10km N of Test");
        assertThat(saved.getEventTime()).isEqualTo(Instant.ofEpochMilli(1700000000000L));
        assertThat(saved.getLongitude()).isEqualByComparingTo("-120.5");
        assertThat(saved.getLatitude()).isEqualByComparingTo("38.2");
        assertThat(saved.getDepth()).isEqualByComparingTo("10.0");
    }

    @Test
    void getAll_returnsMappedDtos() {
        Earthquake entity = sampleEntity("ext-1");
        when(earthquakeRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

        List<EarthquakeDto> result = service.getAll(Optional.empty(), Optional.empty());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().externalId()).isEqualTo("ext-1");
    }
}
