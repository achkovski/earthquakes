package com.kirilachkovski.earthquakes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error payload returned by the API")
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "HTTP reason phrase", example = "Not Found")
        String error,

        @Schema(description = "Readable explanation of the failure", example = "Earthquake with id 42 not found")
        String message
) {
}
