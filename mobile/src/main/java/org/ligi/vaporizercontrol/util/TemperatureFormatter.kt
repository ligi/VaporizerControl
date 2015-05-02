package org.ligi.vaporizercontrol.util

import android.content.Context
import org.ligi.vaporizercontrol.App
import org.ligi.vaporizercontrol.Settings

public class TemperatureFormatter {
    companion object {
        fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

        public fun getTempDegrees(temp: Int, temperatureForamt: Int): Int {
            return when (temperatureForamt) {
                Settings.TEMPERATURE_FAHRENHEIT -> temp * 5 / 9
                Settings.TEMPERATURE_KELVIN -> temp - 2731
                else -> temp
            }
        }

        public fun getFormattedTemp(ctx: Context, temp: Int?, absolute: Boolean): String {
            val tempFormat = getSettings(ctx).getTemperatureFormat();
            return getFormattedTemp(ctx, temp, tempFormat, absolute);
        }

        private fun getSettings(ctx: Context) = (ctx.getApplicationContext() as App).getSettings()

        public fun getFormattedTemp(ctx: Context, temp: Int?, temperatureFormat: Int, absolute: Boolean): String {
            var valString: String = if (temp == null) {
                "?"
            } else {
                when (temperatureFormat) {
                    Settings.TEMPERATURE_FAHRENHEIT -> temp * 0.18f + if (absolute) 32 else 0
                    Settings.TEMPERATURE_KELVIN -> 0.1f * temp + (if (absolute) 273.15f else 1f)
                    else -> 0.1f * temp
                }.format(if (getSettings(ctx).isPreciseWanted()) 1 else 0)
            }

            if (!getSettings(ctx).isDisplayUnitWanted()) {
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
