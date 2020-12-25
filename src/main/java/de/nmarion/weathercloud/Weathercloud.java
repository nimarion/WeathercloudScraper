package de.nmarion.weathercloud;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nmarion.weathercloud.database.Database;
import de.nmarion.weathercloud.database.MySqlDatabase;
import io.sentry.Sentry;

public class Weathercloud {

    private static final Logger logger = LoggerFactory.getLogger(Weathercloud.class);

    private final Database database;
    private final List<String> deviceIds;

    public Weathercloud() {
        final long startTime = System.currentTimeMillis();
        logger.info("Starting WeathercloudScraper");

        if (Configuration.DEVICES == null) {
            throw new RuntimeException("Enviroment variable DEVICES is missing!");
        }
        deviceIds = Arrays.asList(Configuration.DEVICES.split(" "));

        database = new MySqlDatabase(Configuration.DATABASE_HOST, Configuration.DATABASE_USER,
                Configuration.DATABASE_PASSWORD,
                Configuration.DATABASE_NAME == null ? "WeathercloudScraper" : Configuration.DATABASE_NAME,
                Configuration.DATABASE_PORT == null ? 3306 : Integer.valueOf(Configuration.DATABASE_PORT));
        logger.info("Database-Connection set up!");

        final Timer timer = new Timer("Scrape Timer");
        timer.schedule(new WeathercloudScraper(this), 100, TimeUnit.MINUTES.toMillis(10));
        logger.info("Timer Task started");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                timer.cancel();
                database.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }));

        logger.info(String.format("Startup finished in %dms!", System.currentTimeMillis() - startTime));
    }

     /**
     * @return the database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * @return the deviceIds
     */
    public List<String> getDeviceIds() {
        return deviceIds;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    public static void main(String... args) {
        if (System.getenv("SENTRY_DSN") != null || System.getProperty("sentry.properties") != null) {
            Sentry.init();
        }
        new Weathercloud();
    }
   
}
