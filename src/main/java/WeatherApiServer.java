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

    public static String setUrlString() {
        StringBuilder urlString = new StringBuilder(String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=", coordinates.get(0), coordinates.get(1)));
        for (int i = 0; i < types.size(); i++) {
            if (!weatherType.equals(types.get(i))) {
                urlString.append(types.get(i));

                if (i != types.size() - 1) {
                    urlString.append(",");
                }
            }

        }
        urlString.append("&appid=").append(apiKey).append("&units=metric");
        return urlString.toString();
    }

    public static void run() {
        String url = WeatherApiServer.setUrlString();
        System.out.println(url);
    }
}
