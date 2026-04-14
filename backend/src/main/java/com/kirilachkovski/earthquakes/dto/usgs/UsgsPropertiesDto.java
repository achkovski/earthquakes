package com.kirilachkovski.earthquakes.dto.usgs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

//properties of feature
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsgsPropertiesDto(
        BigDecimal mag,
        String place,
        Long time,
        String magType,
        String title
) {
}
