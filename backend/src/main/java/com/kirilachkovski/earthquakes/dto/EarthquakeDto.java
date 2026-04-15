package com.kirilachkovski.earthquakes.dto;

import com.kirilachkovski.earthquakes.entity.Earthquake;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Earthquake event as exposed by the API")
public record EarthquakeDto(

        @Schema(description = "Internal database identifier", example = "42")
        Long id,

        @Schema(description = "Stable identifier assigned by USGS", example = "us7000abcd")
        String externalId,

        @Schema(description = "Magnitude on the reported scale", example = "5.2")
        BigDecimal magnitude,

        @Schema(description = "Magnitude scale used for the reading", example = "mww")
        String magType,

        @Schema(description = "Human-readable location description", example = "22 km W of Cobano, Costa Rica")
        String place,

        @Schema(description = "USGS-provided event title (usually magnitude + place)", example = "M 5.2 - 22 km W of Cobano, Costa Rica")
        String title,

        @Schema(description = "Event time in UTC", example = "2026-03-14T08:12:45Z")
        Instant eventTime,

        @Schema(description = "Longitude in decimal degrees", example = "-85.321")
        BigDecimal longitude,

        @Schema(description = "Latitude in decimal degrees", example = "9.654")
        BigDecimal latitude,

        @Schema(description = "Depth of the hypocenter in kilometers", example = "10.5")
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
