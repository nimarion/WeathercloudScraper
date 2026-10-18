package de.nmarion.weathercloud;

import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeathercloudScraper implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WeathercloudScraper.class);
    private static final String DEFAULT_BASE_URL = "https://app.weathercloud.net";
    private static final Gson GSON = new Gson();

    private final List<String> deviceIds;
    private final WeatherMetrics metrics;
    private final HttpClient httpClient;
    private final String baseUrl;

    public WeathercloudScraper(final List<String> deviceIds, final WeatherMetrics metrics) {
        this(
                deviceIds,
                metrics,
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build(),
                DEFAULT_BASE_URL
        );
    }

    public WeathercloudScraper(
            final List<String> deviceIds,
            final WeatherMetrics metrics,
            final HttpClient httpClient,
            final String baseUrl
    ) {
        this.deviceIds = List.copyOf(deviceIds);
        this.metrics = metrics;
        this.httpClient = httpClient;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public void run() {
        for (final String device : deviceIds) {
            scrapeDevice(device);
        }
    }

    public CompletableFuture<WeatherData> scrapeDevice(final String device) {
        final long startNanos = System.nanoTime();
        return loadCurrentData(device).whenComplete((data, throwable) -> {
            final double durationSeconds = (System.nanoTime() - startNanos) / 1_000_000_000.0;
            if (throwable != null || data == null) {
                metrics.recordScrapeResult(device, false, durationSeconds);
                logger.warn("Failed to scrape data for device {}: {}", device,
                        throwable != null ? throwable.getMessage() : "Empty response");
                return;
            }
            metrics.update(device, data);
            metrics.recordScrapeResult(device, true, durationSeconds);
            logger.info("Updated Prometheus metrics for device {} (temp={}°C, hum={}%)",
                    device, data.temp(), data.hum());
        });
    }

    public CompletableFuture<WeatherData> loadCurrentData(final String deviceId) {
        final String endpointType = isMetarCode(deviceId) ? "metar" : "device";
        final String encodedCode = URLEncoder.encode(deviceId, StandardCharsets.UTF_8);
        final URI uri = URI.create(baseUrl + "/" + endpointType + "/values?code=" + encodedCode);

        final HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .timeout(Duration.ofSeconds(15))
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "Mozilla/5.0 (compatible; WeathercloudScraper/1.0)")
                .header("Accept", "application/json")
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new IllegalStateException("Unexpected HTTP status " + response.statusCode());
                    }
                    final String body = response.body();
                    if (body == null || body.isBlank() || body.trim().equals("[]")) {
                        throw new IllegalStateException("Response body is empty or station is offline");
                    }
                    final WeatherData data = GSON.fromJson(body, WeatherData.class);
                    if (data == null || data.epoch() == null) {
                        throw new IllegalStateException("Invalid WeatherData payload: " + body);
                    }
                    return data;
                });
    }

    private static boolean isMetarCode(final String id) {
        return id != null && id.length() == 4 && id.chars().allMatch(Character::isLetter);
    }
}
