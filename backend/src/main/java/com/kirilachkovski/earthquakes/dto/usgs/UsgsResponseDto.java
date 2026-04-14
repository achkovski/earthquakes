package com.kirilachkovski.earthquakes.dto.usgs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

//response for service
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsgsResponseDto(List<UsgsFeatureDto> features) {
}
