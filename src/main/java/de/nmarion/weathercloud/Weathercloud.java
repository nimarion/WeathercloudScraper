package de.nmarion.weathercloud;

import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weathercloud implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(Weathercloud.class);

    private final Configuration configuration;
    private final WeatherMetrics metrics;
    private final HTTPServer httpServer;
    private final ScheduledExecutorService scheduler;

    public Weathercloud() throws IOException {
        this(Configuration.load());
    }

    public Weathercloud(final Configuration configuration) throws IOException {
        final long startTime = System.currentTimeMillis();
        logger.info("Starting WeathercloudScraper...");

        this.configuration = configuration;

        JvmMetrics.builder().register();
        this.metrics = new WeatherMetrics();

        this.httpServer = HTTPServer.builder()
                .port(configuration.metricsPort())
                .buildAndStart();
        logger.info("Prometheus HTTP server listening on port {} (/metrics)", httpServer.getPort());

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread thread = new Thread(r, "weathercloud-scraper");
            thread.setDaemon(false);
            return thread;
        });

        final WeathercloudScraper scraper = new WeathercloudScraper(configuration.devices(), metrics);
        scheduler.scheduleAtFixedRate(
                scraper,
                0L,
                configuration.scrapeIntervalSeconds(),
                TimeUnit.SECONDS
        );
        logger.info("Scheduled scraper for {} device(s) every {}s",
                configuration.devices().size(), configuration.scrapeIntervalSeconds());

        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "weathercloud-shutdown"));

        logger.info("Startup finished in {}ms!", System.currentTimeMillis() - startTime);
    }

    public List<String> getDeviceIds() {
        return configuration.devices();
    }

    public WeatherMetrics getMetrics() {
        return metrics;
    }

    public int getMetricsPort() {
        return httpServer.getPort();
    }

    @Override
    public void close() {
        logger.info("Shutting down WeathercloudScraper...");
        scheduler.shutdownNow();
        httpServer.close();
    }

    public static void main(final String... args) {
        try {
            new Weathercloud();
        } catch (final Exception ex) {
            logger.error("Failed to start WeathercloudScraper", ex);
            System.exit(1);
        }
    }
}
