package com.kirilachkovski.earthquakes.exception;

public class InvalidEarthquakeDataException extends RuntimeException {

    public InvalidEarthquakeDataException(String message) {
        super(message);
    }

    public InvalidEarthquakeDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
