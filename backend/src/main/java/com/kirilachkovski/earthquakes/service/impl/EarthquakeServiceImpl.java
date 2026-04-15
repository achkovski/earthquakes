package com.kirilachkovski.earthquakes.service.impl;

import com.kirilachkovski.earthquakes.client.UsgsEarthquakeClient;
import com.kirilachkovski.earthquakes.dto.EarthquakeDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsFeatureDto;
import com.kirilachkovski.earthquakes.dto.usgs.UsgsResponseDto;
import com.kirilachkovski.earthquakes.entity.Earthquake;
import com.kirilachkovski.earthquakes.repository.EarthquakeRepository;
import com.kirilachkovski.earthquakes.service.EarthquakeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EarthquakeServiceImpl implements EarthquakeService {

    private final EarthquakeRepository earthquakeRepository;
    private final UsgsEarthquakeClient usgsEarthquakeClient;

    @Override
    public List<EarthquakeDto> getAll(Optional<Instant> after, Optional<BigDecimal> minMagnitude) {
        Specification<Earthquake> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            after.ifPresent(value -> predicates.add(
                    builder.greaterThanOrEqualTo(root.get("eventTime"), value)
            ));

            minMagnitude.ifPresent(value -> predicates.add(
                    builder.greaterThanOrEqualTo(root.get("magnitude"), value)
            ));

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        return earthquakeRepository.findAll(spec).stream()
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

    @Override
    @Transactional
    public int refreshFromUsgs() {
        UsgsResponseDto response = usgsEarthquakeClient.fetch();
        Set<String> existingIds = earthquakeRepository.findAllExternalIds();

        List<Earthquake> toSave = response.features().stream()
                .filter(this::isValid)
                .filter(feature -> !existingIds.contains(feature.id()))
                .map(this::toEntity)
                .toList();

        earthquakeRepository.saveAll(toSave);

        int invalid = (int) response.features().stream().filter(f -> !isValid(f)).count();
        int duplicates = response.features().size() - toSave.size() - invalid;
        log.info("Refreshed earthquakes from USGS. Saved {} new, skipped {} duplicates, skipped {} invalid.",
                toSave.size(), duplicates, invalid);
        return toSave.size();
    }

    private boolean isValid(UsgsFeatureDto feature) {
        if (feature == null || feature.id() == null || feature.properties() == null || feature.geometry() == null) {
            return false;
        }
        if (feature.properties().mag() == null
                || feature.properties().time() == null
                || feature.properties().place() == null) {
            return false;
        }
        List<BigDecimal> coords = feature.geometry().coordinates();
        return coords != null && coords.size() >= 2 && coords.get(0) != null && coords.get(1) != null;
    }

    private Earthquake toEntity(UsgsFeatureDto feature) {
        List<BigDecimal> coords = feature.geometry().coordinates();
        return Earthquake.builder()
                .externalId(feature.id())
                .magnitude(feature.properties().mag())
                .magType(feature.properties().magType())
                .place(feature.properties().place())
                .title(feature.properties().title())
                .eventTime(Instant.ofEpochMilli(feature.properties().time()))
                .longitude(coords.get(0))
                .latitude(coords.get(1))
                .depth(coords.size() >= 3 ? coords.get(2) : null)
                .build();
    }
}
