package de.nmarion.weathercloud;

public class WeatherData {

    private final long epoch;
    private final float bar;
    private final float chill;
    private final float dew;
    private final float heat;
    private final int hum;
    private final float rain;
    private final float temp;
    private final int wdir;
    private final float wspd;
    private final float wspdhi;

    /**
     * @return the epoch
     */
    public long getEpoch() {
        return epoch;
    }

    /**
     * @return the bar
     */
    public float getBar() {
        return bar;
    }

    /**
     * @return the chill
     */
    public float getChill() {
        return chill;
    }

    /**
     * @return the dew
     */
    public float getDew() {
        return dew;
    }

    /**
     * @return the heat
     */
    public float getHeat() {
        return heat;
    }

    /**
     * @return the hum
     */
    public int getHum() {
        return hum;
    }

    /**
     * @return the rain
     */
    public float getRain() {
        return rain;
    }

    /**
     * @return the temp
     */
    public float getTemp() {
        return temp;
    }

    /**
     * @return the wdir
     */
    public int getWdir() {
        return wdir;
    }

    /**
     * @return the wspd
     */
    public float getWspd() {
        return wspd;
    }

    /**
     * @return the wspdhi
     */
    public float getWspdhi() {
        return wspdhi;
    }

    private WeatherData(long epoch, float bar, float chill, float dew, float heat, int hum, float rain, float temp,
            int wdir, float wspd, float wspdhi) {
        this.epoch = epoch;
        this.bar = bar;
        this.chill = chill;
        this.dew = dew;
        this.heat = heat;
        this.hum = hum;
        this.rain = rain;
        this.temp = temp;
        this.wdir = wdir;
        this.wspd = wspd;
        this.wspdhi = wspdhi;
    }
}
