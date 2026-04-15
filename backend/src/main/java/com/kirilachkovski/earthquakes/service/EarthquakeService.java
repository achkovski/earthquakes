package com.kirilachkovski.earthquakes.service;

import com.kirilachkovski.earthquakes.dto.EarthquakeDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EarthquakeService {

    List<EarthquakeDto> getAll(Optional<Instant> after, Optional<BigDecimal> minMagnitude);

    EarthquakeDto getById(Long id);

    void deleteById(Long id);

    int refreshFromUsgs();
}
