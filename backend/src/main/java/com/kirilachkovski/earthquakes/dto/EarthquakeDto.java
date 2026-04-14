package com.kirilachkovski.earthquakes.dto;

import com.kirilachkovski.earthquakes.entity.Earthquake;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record EarthquakeDto(
        Long id,
        String externalId,
        BigDecimal magnitude,
        String magType,
        String place,
        String title,
        OffsetDateTime eventTime,
        BigDecimal longitude,
        BigDecimal latitude,
        BigDecimal depth
) {

    public static EarthquakeDto from(Earthquake entity) {
        return new EarthquakeDto(
                entity.getId(),
                entity.getExternalId(),
                entity.getMagnitude(),
                entity.getMagType(),
                entity.getPlace(),
                entity.getTitle(),
                entity.getEventTime(),
                entity.getLongitude(),
                entity.getLatitude(),
                entity.getDepth()
        );
    }
}
