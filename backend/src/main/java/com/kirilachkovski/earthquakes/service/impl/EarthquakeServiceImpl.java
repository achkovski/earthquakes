package com.kirilachkovski.earthquakes.service.impl;

import com.kirilachkovski.earthquakes.dto.EarthquakeDto;
import com.kirilachkovski.earthquakes.entity.Earthquake;
import com.kirilachkovski.earthquakes.repository.EarthquakeRepository;
import com.kirilachkovski.earthquakes.service.EarthquakeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EarthquakeServiceImpl implements EarthquakeService {

    private final EarthquakeRepository earthquakeRepository;

    @Override
    public List<EarthquakeDto> getAll() {
        return earthquakeRepository.findAll().stream()
                .map(EarthquakeDto::from)
                .toList();
    }

    @Override
    public EarthquakeDto getById(Long id) {
        Earthquake earthquake = earthquakeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Earthquake data not found for id: " + id));
        return EarthquakeDto.from(earthquake);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!earthquakeRepository.existsById(id)) {
            throw new EntityNotFoundException("Earthquake data not found for id: " + id);
        }
        earthquakeRepository.deleteById(id);
        log.info("Deleted earthquake with id: {}", id);
    }
}
