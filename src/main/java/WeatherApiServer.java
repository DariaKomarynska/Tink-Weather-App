import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class WeatherApiServer {
    final static private String apiKey = System.getenv("API_KEY");
    public static String weatherType;
    public static ArrayList<String> coordinates;


    public WeatherApiServer(String weatherType, ArrayList<String> coordinates) {
        WeatherApiServer.weatherType = weatherType;
        WeatherApiServer.coordinates = coordinates;
    }

    public static String setUrlString(String newType) {
        StringBuilder urlString = new StringBuilder(String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=",
                coordinates.get(0), coordinates.get(1)));
        int i = 0;
        for (WeatherType wType : WeatherType.values()) {
            if (!newType.equals(wType.getName())) {
                urlString.append(wType.getName());
                if (i != WeatherType.values().length - 1) {
                    urlString.append(",");

                }
            }
            i++;
        }
        urlString.append("&appid=").append(apiKey).append("&units=metric");
        return urlString.toString();
    }


    public JSONObject getWeatherData(String type) {
        String urlString = WeatherApiServer.setUrlString(type);
        System.out.println(urlString);
        JSONObject resultData = new JSONObject();
        try {
            StringBuilder data = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            reader.close();

            resultData = new JSONObject(data.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return resultData;
    }
}

