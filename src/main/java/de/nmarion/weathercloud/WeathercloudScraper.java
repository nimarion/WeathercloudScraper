package de.nmarion.weathercloud;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeathercloudScraper extends TimerTask{

    private static final Gson GSON = new Gson();
    private final OkHttpClient okHttpClient;

    private final Weathercloud weathercloud;

    public WeathercloudScraper(final Weathercloud weathercloud){
        this.weathercloud = weathercloud;
        this.okHttpClient = new OkHttpClient();
    }
    
    @Override
    public void run() {
        for(String device : weathercloud.getDeviceIds()){
            loadCurrentData(Long.valueOf(device)).whenComplete((data, throwable) -> {
                long timestamp = data.getEpoch();
                float temperature = data.getTemp();
                int humidity = data.getHum();
                float bar = data.getBar();
                float dew = data.getDew();
                float windchill = data.getChill();
                float rain = data.getRain();
                float windspeed = data.getWspd();
                int winddirection = data.getWdir();
                float gustwind = data.getWspdhi();
                weathercloud.getDatabase().insert(Long.valueOf(device), timestamp, temperature, humidity, bar, dew, windchill, rain, windspeed, winddirection, gustwind);
                weathercloud.getLogger().info("Inserted data for device " + device);
            });
        }
    }

    private CompletableFuture<WeatherData> loadCurrentData(long deviceId) {
        final CompletableFuture<WeatherData> future = new CompletableFuture<WeatherData>();
        final Request request = new Request.Builder().url("https://app.weathercloud.net/device/values?code=" + deviceId)
                .header("X-Requested-With", "XMLHttpRequest").build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException exception) {
                future.completeExceptionally(exception);
            }

            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                if(body.isEmpty() || body.equals("[]")){
                    future.completeExceptionally(new NullPointerException("Body is empty"));
                } else {
                    future.complete(GSON.fromJson(body, WeatherData.class));
                }
            }
        });
        return future;
    }
    
}
