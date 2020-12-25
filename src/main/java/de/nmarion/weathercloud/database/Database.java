package de.nmarion.weathercloud.database;

public interface Database extends AutoCloseable {

    public void insert(long device, long timestamp, float temperature, int humidity, float bar, float dew, float windchill, float rain, float windspeed, int winddirection, float gustwind);
    
}
