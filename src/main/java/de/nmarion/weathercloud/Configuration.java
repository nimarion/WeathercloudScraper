package de.nmarion.weathercloud;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import java.util.List;

public record Configuration(
        List<String> devices,
        int metricsPort,
        long scrapeIntervalSeconds
) {

    public static final int DEFAULT_METRICS_PORT = 8080;
    public static final long DEFAULT_SCRAPE_INTERVAL_SECONDS = 600L;

    public static Configuration load() {
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        final String rawDevices = getenv("DEVICES", dotenv);
        final List<String> devices = parseDevices(rawDevices);

        String rawPort = getenv("METRICS_PORT", dotenv);
        if (rawPort == null || rawPort.isBlank()) {
            rawPort = getenv("PORT", dotenv);
        }
        final int metricsPort = parsePort(rawPort, DEFAULT_METRICS_PORT);

        final String rawInterval = getenv("SCRAPE_INTERVAL", dotenv);
        final long scrapeIntervalSeconds = parsePositiveLong(rawInterval, DEFAULT_SCRAPE_INTERVAL_SECONDS, "SCRAPE_INTERVAL");

        return new Configuration(devices, metricsPort, scrapeIntervalSeconds);
    }

    public static List<String> parseDevices(final String rawDevices) {
        if (rawDevices == null || rawDevices.isBlank()) {
            throw new IllegalArgumentException("Environment variable DEVICES is missing or empty!");
        }
        final List<String> devices = Arrays.stream(rawDevices.trim().split("[\\s,]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (devices.isEmpty()) {
            throw new IllegalArgumentException("Environment variable DEVICES contains no valid device IDs!");
        }
        return devices;
    }

    private static int parsePort(final String value, final int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            final int port = Integer.parseInt(value.trim());
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("METRICS_PORT must be between 1 and 65535, got: " + value);
            }
            return port;
        } catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer for METRICS_PORT: " + value, ex);
        }
    }

    private static long parsePositiveLong(final String value, final long defaultValue, final String name) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            final long parsed = Long.parseLong(value.trim());
            if (parsed <= 0) {
                throw new IllegalArgumentException(name + " must be positive, got: " + value);
            }
            return parsed;
        } catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number for " + name + ": " + value, ex);
        }
    }

    private static String getenv(final String name, final Dotenv dotenv) {
        final String sysEnv = System.getenv(name);
        if (sysEnv != null) {
            return sysEnv;
        }
        return dotenv.get(name);
    }
}