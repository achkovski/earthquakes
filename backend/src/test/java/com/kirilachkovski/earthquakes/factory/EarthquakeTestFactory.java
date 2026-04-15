package com.kirilachkovski.earthquakes.factory;

import com.kirilachkovski.earthquakes.dto.EarthquakeDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsFeatureDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsGeometryDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsPropertiesDto;
import com.kirilachkovski.earthquakes.entity.Earthquake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class EarthquakeTestFactory {

    private EarthquakeTestFactory() {
    }

    public static Earthquake sampleEntity(String externalId) {
        return Earthquake.builder()
                .id(1L)
                .externalId(externalId)
                .magnitude(new BigDecimal("4.5"))
                .magType("mb")
                .place("somewhere")
                .title("M 4.5")
                .eventTime(Instant.ofEpochMilli(1700000000000L))
                .longitude(new BigDecimal("-120.0"))
                .latitude(new BigDecimal("38.0"))
                .depth(new BigDecimal("10.0"))
                .build();
    }

    public static EarthquakeDto sampleDto(String externalId) {
        return EarthquakeDto.from(sampleEntity(externalId));
    }

    public static UsgsFeatureDto validFeature(String id) {
        return new UsgsFeatureDto(
                id,
                new UsgsPropertiesDto(
                        new BigDecimal("4.5"),
                        "somewhere",
                        1700000000000L,
                        "mb",
                        "M 4.5"
                ),
                new UsgsGeometryDto(List.of(
                        new BigDecimal("-120.0"),
                        new BigDecimal("38.0"),
                        new BigDecimal("10.0")
                ))
        );
    }
}
