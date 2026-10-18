package de.nmarion.weathercloud;

import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

public class WeatherMetrics {

    private static final String DEVICE_LABEL = "device";

    private final Gauge lastUpdateTimestampSeconds;
    private final Gauge temperatureCelsius;
    private final Gauge temperatureIndoorCelsius;
    private final Gauge humidityPercent;
    private final Gauge humidityIndoorPercent;
    private final Gauge pressureHpa;
    private final Gauge dewPointCelsius;
    private final Gauge dewPointIndoorCelsius;
    private final Gauge windChillCelsius;
    private final Gauge heatIndexCelsius;
    private final Gauge heatIndexIndoorCelsius;
    private final Gauge thwIndexCelsius;
    private final Gauge rainDailyMm;
    private final Gauge rainRateMmPerHour;
    private final Gauge windSpeedMetersPerSecond;
    private final Gauge windGustMetersPerSecond;
    private final Gauge windSpeedAvgMetersPerSecond;
    private final Gauge windDirectionDegrees;
    private final Gauge windDirectionAvgDegrees;
    private final Gauge solarRadiationWattsPerSquareMeter;
    private final Gauge uvIndex;
    private final Gauge visibilityMeters;
    private final Gauge scrapeSuccess;
    private final Gauge scrapeDurationSeconds;

    public WeatherMetrics() {
        this(PrometheusRegistry.defaultRegistry);
    }

    public WeatherMetrics(final PrometheusRegistry registry) {
        this.lastUpdateTimestampSeconds = Gauge.builder()
                .name("weathercloud_last_update_timestamp_seconds")
                .help("Unix epoch timestamp in seconds of the station's last update")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.temperatureCelsius = Gauge.builder()
                .name("weathercloud_temperature_celsius")
                .help("Current outdoor temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.temperatureIndoorCelsius = Gauge.builder()
                .name("weathercloud_temperature_indoor_celsius")
                .help("Current indoor temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.humidityPercent = Gauge.builder()
                .name("weathercloud_humidity_percent")
                .help("Current outdoor relative humidity in percent")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.humidityIndoorPercent = Gauge.builder()
                .name("weathercloud_humidity_indoor_percent")
                .help("Current indoor relative humidity in percent")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.pressureHpa = Gauge.builder()
                .name("weathercloud_pressure_hpa")
                .help("Current barometric pressure in hectopascals (hPa)")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.dewPointCelsius = Gauge.builder()
                .name("weathercloud_dew_point_celsius")
                .help("Current outdoor dew point temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.dewPointIndoorCelsius = Gauge.builder()
                .name("weathercloud_dew_point_indoor_celsius")
                .help("Current indoor dew point temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.windChillCelsius = Gauge.builder()
                .name("weathercloud_wind_chill_celsius")
                .help("Current wind chill temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.heatIndexCelsius = Gauge.builder()
                .name("weathercloud_heat_index_celsius")
                .help("Current outdoor heat index temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.heatIndexIndoorCelsius = Gauge.builder()
                .name("weathercloud_heat_index_indoor_celsius")
                .help("Current indoor heat index temperature in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.thwIndexCelsius = Gauge.builder()
                .name("weathercloud_thw_index_celsius")
                .help("Current Temperature-Humidity-Wind (THW) index in degrees Celsius")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.rainDailyMm = Gauge.builder()
                .name("weathercloud_rain_daily_mm")
                .help("Daily accumulated rainfall in millimeters")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.rainRateMmPerHour = Gauge.builder()
                .name("weathercloud_rain_rate_mm_per_hour")
                .help("Current rainfall rate in millimeters per hour")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.windSpeedMetersPerSecond = Gauge.builder()
                .name("weathercloud_wind_speed_meters_per_second")
                .help("Current wind speed in meters per second")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.windGustMetersPerSecond = Gauge.builder()
                .name("weathercloud_wind_gust_meters_per_second")
                .help("Current wind gust speed in meters per second")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.windSpeedAvgMetersPerSecond = Gauge.builder()
                .name("weathercloud_wind_speed_avg_meters_per_second")
                .help("Average wind speed in meters per second")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.windDirectionDegrees = Gauge.builder()
                .name("weathercloud_wind_direction_degrees")
                .help("Current wind direction in degrees")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.windDirectionAvgDegrees = Gauge.builder()
                .name("weathercloud_wind_direction_avg_degrees")
                .help("Average wind direction in degrees")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.solarRadiationWattsPerSquareMeter = Gauge.builder()
                .name("weathercloud_solar_radiation_watts_per_square_meter")
                .help("Current solar radiation in watts per square meter")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.uvIndex = Gauge.builder()
                .name("weathercloud_uv_index")
                .help("Current UV index")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.visibilityMeters = Gauge.builder()
                .name("weathercloud_visibility_meters")
                .help("Current visibility in meters")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.scrapeSuccess = Gauge.builder()
                .name("weathercloud_scrape_success")
                .help("Whether the last scrape for the device was successful (1 = success, 0 = failure)")
                .labelNames(DEVICE_LABEL)
                .register(registry);

        this.scrapeDurationSeconds = Gauge.builder()
                .name("weathercloud_scrape_duration_seconds")
                .help("Duration of the last Weathercloud scrape request in seconds")
                .labelNames(DEVICE_LABEL)
                .register(registry);
    }

    public void update(final String device, final WeatherData data) {
        if (data.epoch() != null) {
            lastUpdateTimestampSeconds.labelValues(device).set(data.epoch().doubleValue());
        }
        setIfPresent(temperatureCelsius, device, data.temp());
        setIfPresent(temperatureIndoorCelsius, device, data.tempin());
        setIfPresent(humidityPercent, device, data.hum());
        setIfPresent(humidityIndoorPercent, device, data.humin());
        setIfPresent(pressureHpa, device, data.bar());
        setIfPresent(dewPointCelsius, device, data.dew());
        setIfPresent(dewPointIndoorCelsius, device, data.dewin());
        setIfPresent(windChillCelsius, device, data.chill());
        setIfPresent(heatIndexCelsius, device, data.heat());
        setIfPresent(heatIndexIndoorCelsius, device, data.heatin());
        setIfPresent(thwIndexCelsius, device, data.thw());
        setIfPresent(rainDailyMm, device, data.rain());
        setIfPresent(rainRateMmPerHour, device, data.rainrate());
        setIfPresent(windSpeedMetersPerSecond, device, data.wspd());
        setIfPresent(windGustMetersPerSecond, device, data.wspdhi());
        setIfPresent(windSpeedAvgMetersPerSecond, device, data.wspdavg());
        setIfPresent(windDirectionDegrees, device, data.wdir());
        setIfPresent(windDirectionAvgDegrees, device, data.wdiravg());
        setIfPresent(solarRadiationWattsPerSquareMeter, device, data.solarrad());
        setIfPresent(uvIndex, device, data.uvi());
        if (data.vis() != null) {
            visibilityMeters.labelValues(device).set(data.vis() * 100.0);
        }
    }

    public void recordScrapeResult(final String device, final boolean success, final double durationSeconds) {
        scrapeSuccess.labelValues(device).set(success ? 1.0 : 0.0);
        scrapeDurationSeconds.labelValues(device).set(durationSeconds);
    }

    private static void setIfPresent(final Gauge gauge, final String device, final Double value) {
        if (value != null && !value.isNaN() && !value.isInfinite()) {
            gauge.labelValues(device).set(value);
        }
    }
}
