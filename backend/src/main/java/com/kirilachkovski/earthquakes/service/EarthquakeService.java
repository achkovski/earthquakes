package com.kirilachkovski.earthquakes.service;

import com.kirilachkovski.earthquakes.dto.EarthquakeDto;

import java.util.List;

public interface EarthquakeService {

    List<EarthquakeDto> getAll();

    EarthquakeDto getById(Long id);

    void deleteById(Long id);

    int refreshFromUsgs();
}
