package com.kirilachkovski.earthquakes.dto.usgs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

//location
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsgsGeometryDto(List<BigDecimal> coordinates) {
}
