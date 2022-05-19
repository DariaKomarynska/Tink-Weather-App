import java.util.ArrayList;

public class WeatherApiServer {

    private static String urlString(String city, String weatherType, ArrayList<String> coordinates) {
        String apiKey = System.getenv("API_KEY");
        return "url";
    }

    public static void run() {
        System.out.println("Weather forecast");
    }
}
