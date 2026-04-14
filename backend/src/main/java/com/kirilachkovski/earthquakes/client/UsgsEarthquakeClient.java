package com.kirilachkovski.earthquakes.client;

import com.kirilachkovski.earthquakes.dto.usgs.UsgsResponseDto;
import com.kirilachkovski.earthquakes.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class UsgsEarthquakeClient {

    private final RestClient usgsRestClient;
    private final String feed;

    public UsgsEarthquakeClient(RestClient usgsRestClient,
                                @Value("${earthquakes.usgs.feed}") String feed) {
        this.usgsRestClient = usgsRestClient;
        this.feed = feed;
    }

    public UsgsResponseDto fetch() {
        log.info("Fetching earthquake data from USGS feed: {}", feed);
        try {
            UsgsResponseDto response = usgsRestClient.get()
                    .uri("/{feed}", feed)
                    .retrieve()
                    .body(UsgsResponseDto.class);

            if (response == null) {
                throw new ExternalApiException("USGS returned an empty response body!");
            }
            return response;
        } catch (RestClientException exception) {
            throw new ExternalApiException("Failed to fetch data from USGS feed: " + feed, exception);
        }
    }
}
