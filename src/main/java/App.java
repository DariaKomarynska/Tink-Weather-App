import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) {
        // if 3 args
        // if file exists
        // if city is in the file
        // if type exists and is daily or hourly
        // get coordinates

        String weatherType = "daily";
        ArrayList<String> coordinates = new ArrayList<>(Arrays.asList("52.237049", "21.017532"));
        WeatherApiServer weatherApiServer = new WeatherApiServer(weatherType, coordinates);
        WeatherApiServer.run();
    }
}
