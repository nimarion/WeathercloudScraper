[![Maintainability](https://api.codeclimate.com/v1/badges/01bcc2760cbb7968b02b/maintainability)](https://codeclimate.com/github/nimarion/WeathercloudScraper/maintainability)
[![Build Status](https://github.com/nimarion/WeathercloudScraper/workflows/Build/badge.svg?branch=master)](https://github.com/nimarion/WeathercloudScraper/actions)

# WeathercloudScraper

A Prometheus exporter that periodically scrapes weather station data from [Weathercloud](https://weathercloud.net/) and exposes metrics at `/metrics`.

![Image of Grafana](https://i.imgur.com/sNFfbaS.png)

## Configuration

Configuration can be provided via environment variables or a `.env` file:

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `DEVICES` | Yes | — | Space- or comma-separated list of Weathercloud device IDs (or 4-letter METAR codes) |
| `METRICS_PORT` | No | `8080` | Port for the Prometheus HTTP server (`/metrics`) |
| `SCRAPE_INTERVAL` | No | `600` | Interval in seconds between Weathercloud API scrapes |

## Exposed Prometheus Metrics

All station metrics include the `device="<device_id>"` label:

- `weathercloud_temperature_celsius`
- `weathercloud_temperature_indoor_celsius`
- `weathercloud_humidity_percent`
- `weathercloud_humidity_indoor_percent`
- `weathercloud_pressure_hpa`
- `weathercloud_dew_point_celsius`
- `weathercloud_dew_point_indoor_celsius`
- `weathercloud_wind_chill_celsius`
- `weathercloud_heat_index_celsius`
- `weathercloud_heat_index_indoor_celsius`
- `weathercloud_thw_index_celsius`
- `weathercloud_rain_daily_mm`
- `weathercloud_rain_rate_mm_per_hour`
- `weathercloud_wind_speed_meters_per_second`
- `weathercloud_wind_gust_meters_per_second`
- `weathercloud_wind_speed_avg_meters_per_second`
- `weathercloud_wind_direction_degrees`
- `weathercloud_wind_direction_avg_degrees`
- `weathercloud_solar_radiation_watts_per_square_meter`
- `weathercloud_uv_index`
- `weathercloud_visibility_meters`
- `weathercloud_last_update_timestamp_seconds`
- `weathercloud_scrape_success`
- `weathercloud_scrape_duration_seconds`

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

<p align="center">
  <img alt="Haha yes " width="250px" src="https://i.imgur.com/5bXJeZt.png">
  <br>
</p>
