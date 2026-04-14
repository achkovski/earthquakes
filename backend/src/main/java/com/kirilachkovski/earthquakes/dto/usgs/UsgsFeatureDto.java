package com.kirilachkovski.earthquakes.dto.usgs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//extraction features
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsgsFeatureDto(
        String id,
        UsgsPropertiesDto properties,
        UsgsGeometryDto geometry
) {
}
