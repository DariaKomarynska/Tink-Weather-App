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
    static final public ArrayList<String> types = new ArrayList<>(Arrays.asList("current", "minutely", "hourly", "daily", "alerts"));


    public WeatherApiServer(String weatherType, ArrayList<String> coordinates) {
        WeatherApiServer.weatherType = weatherType;
        WeatherApiServer.coordinates = coordinates;
    }

    public static String setUrlString(String type) {
        StringBuilder urlString = new StringBuilder(String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=",
                coordinates.get(0), coordinates.get(1)));
        for (int i = 0; i < types.size(); i++) {
            if (!type.equals(types.get(i))) {
                urlString.append(types.get(i));

                if (i != types.size() - 1) {
                    urlString.append(",");
                }
            }

        }
        urlString.append("&appid=").append(apiKey).append("&units=metric");
        return urlString.toString();
    }


    public JSONObject getWeatherData(String type) {
        String urlString = WeatherApiServer.setUrlString(type);
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

