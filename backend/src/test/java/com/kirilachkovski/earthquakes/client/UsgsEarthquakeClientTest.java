package com.kirilachkovski.earthquakes.client;

import com.kirilachkovski.earthquakes.dto.usgs.UsgsResponseDto;
import com.kirilachkovski.earthquakes.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class UsgsEarthquakeClientTest {

    private static final String BASE_URL = "https://earthquake.example.com";
    private static final String FEED = "all_day.geojson";

    private MockRestServiceServer server;
    private UsgsEarthquakeClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new UsgsEarthquakeClient(builder.build(), FEED);
    }

    @Test
    void fetch_returnsResponse_whenApiRespondsWithValidJson() {
        String json = """
                {
                  "features": [
                    {
                      "id": "us1000abcd",
                      "properties": {
                        "mag": 4.5,
                        "place": "10km N of Test",
                        "time": 1700000000000,
                        "magType": "mb",
                        "title": "M 4.5 - 10km N of Test"
                      },
                      "geometry": { "coordinates": [-120.5, 38.2, 10.0] }
                    }
                  ]
                }
                """;

        server.expect(requestTo(BASE_URL + "/" + FEED))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        UsgsResponseDto response = client.fetch();

        assertThat(response).isNotNull();
        assertThat(response.features()).hasSize(1);
        assertThat(response.features().getFirst().id()).isEqualTo("us1000abcd");
        server.verify();
    }

    @Test
    void fetch_throwsExternalApiException_whenApiReturnsServerError() {
        server.expect(requestTo(BASE_URL + "/" + FEED))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.fetch())
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining(FEED);
    }

    @Test
    void fetch_throwsExternalApiException_whenResponseBodyIsEmpty() {
        server.expect(requestTo(BASE_URL + "/" + FEED))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch())
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("empty");
    }
}
