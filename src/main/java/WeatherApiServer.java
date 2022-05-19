import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class WeatherApiServer {
    final static private String apiKey = System.getenv("API_KEY");
    public static String weatherType;
    public static ArrayList<String> coordinates;


    public WeatherApiServer(String weatherType, ArrayList<String> coordinates) {
        WeatherApiServer.weatherType = weatherType;
        WeatherApiServer.coordinates = coordinates;
    }

    public static String setUrlString() {
        StringBuilder urlString = new StringBuilder(String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=",
                coordinates.get(0), coordinates.get(1)));
        int i = 0;
        for (WeatherType wType : WeatherType.values()) {
            if (!weatherType.equals(wType.getName())) {
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


    public JSONArray getWeatherDataByType() {
        String urlString = WeatherApiServer.setUrlString();
        System.out.println(urlString);
        JSONObject allData;
        JSONArray specData = new JSONArray();
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

            allData = new JSONObject(data.toString());
            specData = allData.getJSONArray(weatherType);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return specData;
    }


    public ArrayList<Double> getHourlyTemperatures() {
        JSONArray allData = getWeatherDataByType();
        ArrayList<Double> temperatures = new ArrayList<>();
        for (int i = 0; i < allData.length(); i++) {
            Double temp = null;
            try {
                JSONObject hourData = allData.getJSONObject(i);
                temp = (Double) hourData.get("temp");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            temperatures.add(temp);
        }
        return temperatures;
    }


    public ArrayList<Double> getHourlyAllPoP() {
        JSONArray allData = getWeatherDataByType();
        ArrayList<Double> allPoP = new ArrayList<>();
        for (int i = 0; i < allData.length(); i++) {
            JSONObject hourData;
            Double pop = null;
            try {
                hourData = allData.getJSONObject(i);
                pop = hourData.getDouble("pop");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            allPoP.add(pop);
        }
        return allPoP;
    }


    private double getHourlyMinTemp() {
        ArrayList<Double> temperatures = getHourlyTemperatures();
        return Collections.min(temperatures);
    }


    private double getHourlyMaxTemp() {
        ArrayList<Double> temperatures = getHourlyTemperatures();
        return Collections.max(temperatures);
    }


    private double getHourlyAvgTemp() {
        ArrayList<Double> temperatures = getHourlyTemperatures();
        return temperatures.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    private double getHourlyAvgPoP() {
        ArrayList<Double> allPoP = getHourlyAllPoP();
        return allPoP.stream().mapToDouble(d -> d).average().orElse(0.0);
    }


    public String getHourlyStatistics() {
        double minTemp = getHourlyMinTemp();
        double maxTemp = getHourlyMaxTemp();
        double avgTemp = getHourlyAvgTemp();
        double pop = getHourlyAvgPoP();
        return getStringData(minTemp, maxTemp, avgTemp, pop);
    }

    private static String getStringData(double minTemp, double maxTemp, double avgTemp, double pop) {
        return "Minimum temperature: " + minTemp + "\n"
                + "Maximum temperature: " + maxTemp + "\n"
                + "Average temperature: " + avgTemp + "\n"
                + "Probability of precipitation: " + pop + "\n";
    }

    public String getAllWeatherStatistics() {
        String result = null;
        String statistics;
        if (weatherType.equals(WeatherType.HOURLY.getName())) {
            statistics = getHourlyStatistics();
            result = "Hourly statistics for next 48 hours: \n" + statistics;

        } else if (weatherType.equals(WeatherType.DAILY.getName())) {
            result = "daily";
        }
        return result;
    }

    public void printStatistics() {
        System.out.println(getAllWeatherStatistics());
    }

}

