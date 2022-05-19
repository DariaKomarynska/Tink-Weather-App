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

    public ArrayList<Object> getTemperatures() {
        JSONArray allData = getWeatherDataByType();
        ArrayList<Object> temperatures = new ArrayList<>();
        for (int i = 0; i < allData.length(); i++) {
            Object temp = null;
            try {
                JSONObject tempData = allData.getJSONObject(i);
                temp = tempData.get("temp");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            temperatures.add(temp);
        }
        return temperatures;
    }

    public ArrayList<Double> getAllPoP() {
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

    public ArrayList<String> getDailyStatistics() {
        final int DAYS = 7;
        ArrayList<String> dataString = new ArrayList<>();
        ArrayList<Object> temperatures = getTemperatures();
        ArrayList<Double> allPoPs = getAllPoP();
        double minTemp, maxTemp, avgTemp, pop;
        List<Double> dailyTemps;
        JSONObject jsonTemp;
        String tempData;
        if (temperatures.size() == allPoPs.size() &&
                temperatures.size() >= DAYS) {
            for (int i = 0; i < DAYS; i++) {
                jsonTemp = (JSONObject) temperatures.get(i);
                try {
                    minTemp = jsonTemp.getDouble("min");
                    maxTemp = jsonTemp.getDouble("max");
                    dailyTemps = Arrays.asList(jsonTemp.getDouble("morn"), jsonTemp.getDouble("day"),
                            jsonTemp.getDouble("eve"), jsonTemp.getDouble("night"));
                    avgTemp = dailyTemps.stream().mapToDouble(d -> d).average().orElse(0.0);
                    pop = allPoPs.get(i);
                    tempData = "Day " + (i + 1) + ": \n";
                    tempData += getStringData(minTemp, maxTemp, avgTemp, pop);
                    dataString.add(tempData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataString;
    }

    public ArrayList<Double> getHourlyTemperaturesToDouble() {
        ArrayList<Object> temperatures = getTemperatures();
        ArrayList<Double> result = new ArrayList<>();
        for (Object temp : temperatures) {
            result.add(Double.valueOf(temp.toString()));
        }
        return result;
    }


    private double getHourlyMinTemp(ArrayList<Double> temperatures) {
        return Collections.min(temperatures);
    }

    private double getHourlyMaxTemp(ArrayList<Double> temperatures) {
        return Collections.max(temperatures);
    }

    private double getHourlyAvgTemp(ArrayList<Double> temperatures) {
        return temperatures.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    private double getHourlyAvgPoP() {
        ArrayList<Double> allPoP = getAllPoP();
        return allPoP.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    public String getHourlyStatistics() {
        ArrayList<Double> temperatures = getHourlyTemperaturesToDouble();
        double minTemp = getHourlyMinTemp(temperatures);
        double maxTemp = getHourlyMaxTemp(temperatures);
        double avgTemp = getHourlyAvgTemp(temperatures);
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
            statistics = toStringDailyStatistics();
            result = "Daily statistics for 7 days: \n" + statistics;
        }
        return result;
    }

    public String toStringDailyStatistics(){
        ArrayList<String> statisticsDaily = getDailyStatistics();
        StringBuilder result = new StringBuilder();
        for (String dayWeather : statisticsDaily){
            result.append(dayWeather);
            result.append("\n");
        }
        return result.toString();
    }

    public void printStatistics() {
        System.out.println(getAllWeatherStatistics());
    }

}

