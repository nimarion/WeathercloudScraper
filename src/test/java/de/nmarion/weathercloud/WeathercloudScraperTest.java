package de.nmarion.weathercloud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class WeathercloudScraperTest {

    @Test
    void testParseDevices() {
        assertEquals(List.of("1234567890", "0987654321"), Configuration.parseDevices("1234567890 0987654321"));
        assertEquals(List.of("1234567890", "EDDF"), Configuration.parseDevices("1234567890, EDDF"));
        assertThrows(IllegalArgumentException.class, () -> Configuration.parseDevices(null));
        assertThrows(IllegalArgumentException.class, () -> Configuration.parseDevices("   "));
    }

    @Test
    void testScrapeAndExposePrometheusMetrics() throws Exception {
        final HttpServer mockApi = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        mockApi.createContext("/device/values", exchange -> {
            final String query = exchange.getRequestURI().getQuery();
            final String body;
            if ("code=1111111111".equals(query)) {
                body = """
                        {
                          "epoch": 1792333200,
                          "temp": 18.5,
                          "hum": 64,
                          "bar": 1018.2,
                          "dew": 11.6,
                          "chill": 18.5,
                          "rain": 2.4,
                          "rainrate": 0.0,
                          "wspd": 3.2,
                          "wspdhi": 5.8,
                          "wdir": 240,
                          "vis": 100
                        }
                        """;
            } else {
                body = "[]";
            }
            final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        mockApi.start();

        final PrometheusRegistry registry = new PrometheusRegistry();
        final WeatherMetrics metrics = new WeatherMetrics(registry);
        final String baseUrl = "http://127.0.0.1:" + mockApi.getAddress().getPort();
        final HttpClient httpClient = HttpClient.newHttpClient();

        final WeathercloudScraper scraper = new WeathercloudScraper(
                List.of("1111111111", "2222222222"),
                metrics,
                httpClient,
                baseUrl
        );

        try (HTTPServer prometheusServer = HTTPServer.builder()
                .port(0)
                .registry(registry)
                .buildAndStart()) {

            final WeatherData data = scraper.scrapeDevice("1111111111").join();
            assertNotNull(data);
            assertEquals(18.5, data.temp());
            assertNull(data.solarrad());

            // Offline device returns "[]" -> should complete exceptionally and set scrape_success=0
            assertThrows(Exception.class, () -> scraper.scrapeDevice("2222222222").join());

            final HttpRequest metricsReq = HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + prometheusServer.getPort() + "/metrics")
            ).GET().build();
            final HttpResponse<String> metricsResp = httpClient.send(metricsReq, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, metricsResp.statusCode());
            final String exposition = metricsResp.body();
            assertTrue(exposition.contains("weathercloud_temperature_celsius{device=\"1111111111\"} 18.5"));
            assertTrue(exposition.contains("weathercloud_humidity_percent{device=\"1111111111\"} 64.0"));
            assertTrue(exposition.contains("weathercloud_pressure_hpa{device=\"1111111111\"} 1018.2"));
            assertTrue(exposition.contains("weathercloud_visibility_meters{device=\"1111111111\"} 10000.0"));
            assertTrue(exposition.contains("weathercloud_scrape_success{device=\"1111111111\"} 1.0"));
            assertTrue(exposition.contains("weathercloud_scrape_success{device=\"2222222222\"} 0.0"));
        } finally {
            mockApi.stop(0);
        }
    }
}
