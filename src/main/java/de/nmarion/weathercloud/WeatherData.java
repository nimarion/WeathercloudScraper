package de.nmarion.weathercloud;

public record WeatherData(
        Long epoch,
        Double bar,
        Double chill,
        Double dew,
        Double dewin,
        Double heat,
        Double heatin,
        Double thw,
        Double hum,
        Double humin,
        Double rain,
        Double rainrate,
        Double temp,
        Double tempin,
        Double wdir,
        Double wdiravg,
        Double wspd,
        Double wspdhi,
        Double wspdavg,
        Double solarrad,
        Double uvi,
        Double vis
) {
}
