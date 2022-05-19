import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class App {

    public static ArrayList<String> readCoordinatesFromFile(String path, String inputCity) {
        Scanner scanner = null;
        ArrayList<String> cityCoords = new ArrayList<>();
        try {
            scanner = new Scanner(new File(path));
            scanner.useDelimiter("\n");
            String line;
            while (scanner.hasNext()) {
                line = scanner.next();
                if (line.contains(inputCity)) {
                    String[] cityLine = line.split(", ");
                    cityCoords.add(cityLine[1]);
                    cityCoords.add(cityLine[1]);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File does not exist. Try again!");
        }
        if (scanner != null) {
            scanner.close();
        }
        return cityCoords;
    }

    public static void main(String[] args) {
        final String PATH;
        final String inputWeatherType;
        final String inputCity;

        PATH = "D:\\Weather\\WeatherApp\\src\\main\\java\\Cities";
        inputWeatherType = "daily";
        inputCity = "warsaw";

        ArrayList<String> coordinates = readCoordinatesFromFile(PATH, inputCity);
        if (!coordinates.isEmpty()) {
            if (inputWeatherType.equals(WeatherType.HOURLY.getName())
                    || inputWeatherType.equals(WeatherType.DAILY.getName())) {
                WeatherApiServer weatherApiServer = new WeatherApiServer(inputWeatherType, coordinates);
                weatherApiServer.printStatistics();
            } else {
                System.out.println("Invalid weather type. Try again!");
            }
        } else {
            System.err.println("City is not available. Try again!");
        }

    }

}
