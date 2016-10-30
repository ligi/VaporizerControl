package org.ligi.vaporizercontrol

import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.values_layout.*
import org.ligi.vaporizercontrol.model.Settings
import org.ligi.vaporizercontrol.model.VaporizerData
import org.ligi.vaporizercontrol.util.TemperatureFormatter.getFormattedTemp

class VaporizerDataBinder(val context: Activity, private val settings: Settings) {

    fun bind(data: VaporizerData) {
        context.battery.text = if (data.batteryPercentage == null) "?" else "" + data.batteryPercentage!! + "%"

        context.temperature.text = getFormattedTemp(settings, data.currentTemperature, true) + " / "
        context.temperatureSetPoint.text = getFormattedTemp(settings, data.setTemperature, true)
        context.tempBoost.text = "+" + getFormattedTemp(settings, data.boostTemperature, false)
        context.led.text = (if (data.ledPercentage == null) "?" else "" + data.ledPercentage!!) + "%"

        val isNotSeenForSomeTime = System.currentTimeMillis().minus(data.lastDataMillis ?: 0) > 1000
        context.visibility_indicator.visibility = if (isNotSeenForSomeTime) View.VISIBLE else View.INVISIBLE
    }
}
