package com.kirilachkovski.earthquakes.dto;

public record ErrorResponse(int status, String error, String message) {
}
