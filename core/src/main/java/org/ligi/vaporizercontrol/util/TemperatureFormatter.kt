package org.ligi.vaporizercontrol.util

import org.ligi.vaporizercontrol.model.Settings
import java.lang.String.format

object TemperatureFormatter {
    fun Float.format(digits: Int) = format("%.${digits}f", this)!!

    fun getFormattedTemp(settings: Settings, temp: Int?, absolute: Boolean) = getFormattedTemp(settings, temp, settings.temperatureFormat, absolute)

    fun getFormattedTemp(settings: Settings, temp: Int?, temperatureFormat: Int, absolute: Boolean): String {
        val valString: String = if (temp == null) {
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

        return valString + when (temperatureFormat) {
            Settings.TEMPERATURE_FAHRENHEIT -> " °F"
            Settings.TEMPERATURE_KELVIN -> " °K"
            else -> " °C"
        }
    }
}
