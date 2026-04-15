package com.kirilachkovski.earthquakes.controller;

import com.kirilachkovski.earthquakes.dto.EarthquakeDto;
import com.kirilachkovski.earthquakes.dto.ErrorResponse;
import com.kirilachkovski.earthquakes.service.EarthquakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Earthquakes", description = "Query and manage earthquake records sourced from USGS")
public class EarthquakeController {

    private final EarthquakeService earthquakeService;

    @GetMapping
    @Operation(
            summary = "List all earthquakes",
            description = "Returns all stored earthquakes, optionally filtered by minimum event date and/or minimum magnitude."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Earthquakes returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameter",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<EarthquakeDto>> getAll(
            @Parameter(description = "Only include events occurring on or after this date", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,

            @Parameter(description = "Only include events with magnitude greater than or equal to this value", example = "4.5")
            @RequestParam(required = false) BigDecimal minMag) {
        return ResponseEntity.ok(earthquakeService.getAll(
                Optional.ofNullable(after).map(date -> date.atStartOfDay(ZoneOffset.UTC).toInstant()),
                Optional.ofNullable(minMag)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get earthquake by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Earthquake found"),
            @ApiResponse(responseCode = "404", description = "No earthquake data for the given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EarthquakeDto> getById(
            @Parameter(description = "Internal database identifier", example = "42")
            @PathVariable Long id) {
        return ResponseEntity.ok(earthquakeService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete earthquake by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Earthquake deleted"),
            @ApiResponse(responseCode = "404", description = "No earthquake data for the given ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Internal database identifier", example = "42")
            @PathVariable Long id) {
        earthquakeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh from USGS",
            description = "Fetches the latest earthquake feed from USGS and saves it " +
                    "into the local database. Returns the number of records saved."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refresh completed"),
            @ApiResponse(responseCode = "502", description = "USGS unreachable or returned invalid data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Integer>> refresh() {
        int saved = earthquakeService.refreshFromUsgs();
        return ResponseEntity.ok(Map.of("Saved", saved));
    }
}
