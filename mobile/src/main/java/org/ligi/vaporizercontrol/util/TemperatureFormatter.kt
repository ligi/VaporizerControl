package org.ligi.vaporizercontrol.util

import org.ligi.vaporizercontrol.model.Settings

public class TemperatureFormatter {
    companion object {
        fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

        public fun getTempDegrees(temp: Int, temperatureFormat: Int): Int {
            return when (temperatureFormat) {
                Settings.TEMPERATURE_FAHRENHEIT -> temp * 5 / 9
                Settings.TEMPERATURE_KELVIN -> temp - 2731
                else -> temp
            }
        }

        public fun getFormattedTemp(settings: Settings, temp: Int?, absolute: Boolean): String {
            val tempFormat = settings.temperatureFormat;
            return getFormattedTemp(settings, temp, tempFormat, absolute);
        }

        public fun getFormattedTemp(settings: Settings, temp: Int?, temperatureFormat: Int, absolute: Boolean): String {
            var valString: String = if (temp == null) {
                "?"
            } else {
                when (temperatureFormat) {
                    Settings.TEMPERATURE_FAHRENHEIT -> temp * 0.18f + if (absolute) 32 else 0
                    Settings.TEMPERATURE_KELVIN -> 0.1f * temp + (if (absolute) 273.15f else 1f)
                    else -> 0.1f * temp
                }.format(if (settings.isPreciseWanted) 1 else 0)
            }

            if (!settings.isDisplayUnitWanted) {
                return valString
            }

            return when (temperatureFormat) {
                Settings.TEMPERATURE_FAHRENHEIT -> valString + " °F";
                Settings.TEMPERATURE_KELVIN -> valString + " °K";
                else -> valString + " °C";
            }
        }
    }
}
