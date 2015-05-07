package org.ligi.vaporizercontrol.model;

public interface Settings {
    int TEMPERATURE_CELSIUS = 0;
    int TEMPERATURE_FAHRENHEIT = 1;
    int TEMPERATURE_KELVIN = 2;

    int getTemperatureFormat();
    String getAutoConnectMAC();
    boolean isDisplayUnitWanted();
    boolean isPreciseWanted();

}
