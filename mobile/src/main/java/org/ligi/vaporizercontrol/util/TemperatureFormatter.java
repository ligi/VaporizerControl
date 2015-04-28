package org.ligi.vaporizercontrol.util;

import org.ligi.vaporizercontrol.Settings;

public class TemperatureFormatter {
    public static String getFormattedTemp(Integer temp, int temperatureFormat) {
        if (temperatureFormat == Settings.TEMPERATURE_CELSIUS) {
            return getIntegerForTemperature(temp, temperatureFormat) + " °C";
        } else {
            return getIntegerForTemperature(temp, temperatureFormat) + " °F";
        }
    }

    private static String getIntegerForTemperature(Integer temp, int temperatureFormat) {
        if (temp == null) {
            return "?";
        } else {
            if (temperatureFormat == Settings.TEMPERATURE_CELSIUS) {
                return String.valueOf(temp / 10f);
            } else {
                return String.valueOf(((temp * 9) / 5 + 320) / 10f);
            }

        }
    }
}
