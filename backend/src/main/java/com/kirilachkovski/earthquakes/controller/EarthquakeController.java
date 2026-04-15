package com.kirilachkovski.earthquakes.controller;

import com.kirilachkovski.earthquakes.dto.EarthquakeDto;
import com.kirilachkovski.earthquakes.service.EarthquakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/earthquakes")
@RequiredArgsConstructor
public class EarthquakeController {

    private final EarthquakeService earthquakeService;

    @GetMapping
    public ResponseEntity<List<EarthquakeDto>> getAll(
            @RequestParam(required = false) LocalDate after,
            @RequestParam(required = false) BigDecimal minMag) {
        return ResponseEntity.ok(earthquakeService.getAll(
                Optional.ofNullable(after).map(date -> date.atStartOfDay(ZoneOffset.UTC).toInstant()),
                Optional.ofNullable(minMag)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EarthquakeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(earthquakeService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        earthquakeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Integer>> refresh() {
        int saved = earthquakeService.refreshFromUsgs();
        return ResponseEntity.ok(Map.of("Saved", saved));
    }
}
