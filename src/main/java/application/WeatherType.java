package application;

public enum WeatherType {
    CURRENT("current"), MINUTELY("minutely"), HOURLY("hourly"), DAILY("daily"), ALERTS("alerts");

    private final String name;

    WeatherType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
