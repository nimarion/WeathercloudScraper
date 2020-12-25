package de.nmarion.weathercloud.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySqlDatabase implements Database {

    private final HikariDataSource dataSource;

    public MySqlDatabase(final String host, final String username, final String password, final String database,
            final int port) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);

        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `Weather` ( `Timestamp` TIMESTAMP NOT NULL , `Device` BIGINT NOT NULL , `Temperature` FLOAT NOT NULL , `Humidty` INT NOT NULL , `Bar` FLOAT NOT NULL , `Dew` FLOAT NOT NULL , `Windchill` FLOAT NOT NULL , `Rain` FLOAT NOT NULL , `Windspeed` FLOAT NOT NULL , `Winddirection` INT NOT NULL , `Gust` FLOAT NOT NULL )")) {
                preparedStatement.executeUpdate();
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void insert(long device, long timestamp, float temperature, int humidity, float bar, float dew,
            float windchill, float rain, float windspeed, int winddirection, float gustwind) {
        try (final Connection connection = dataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `Weather` (`Timestamp`, `Device`, `Temperature`, `Humidty`, `Bar`, `Dew`, `Windchill`, `Rain`, `Windspeed`, `Winddirection`, `Gust`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                preparedStatement.setTimestamp(1, new Timestamp(timestamp * 1000L));
                preparedStatement.setLong(2, device);
                preparedStatement.setFloat(3, temperature);
                preparedStatement.setInt(4, humidity);
                preparedStatement.setFloat(5, bar);
                preparedStatement.setFloat(6, dew);
                preparedStatement.setFloat(7, windchill);
                preparedStatement.setFloat(8, rain);
                preparedStatement.setFloat(9, windspeed);
                preparedStatement.setInt(10, winddirection);
                preparedStatement.setFloat(11, gustwind);
                preparedStatement.execute();
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }

}
