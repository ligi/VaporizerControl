package org.ligi.vaporizercontrol

import android.app.Activity
import android.widget.TextView
import ligi.org.core.R
import org.ligi.vaporizercontrol.model.Settings
import org.ligi.vaporizercontrol.model.VaporizerData
import org.ligi.vaporizercontrol.util.TemperatureFormatter.getFormattedTemp

class VaporizerDataBinder(context: Activity, private val settings: Settings) {

    internal var battery: TextView
    internal var temperature: TextView
    internal var temperatureSetPoint: TextView
    internal var tempBoost: TextView
    internal var led: TextView

    init {
        battery = context.findViewById(R.id.battery) as TextView
        temperature = context.findViewById(R.id.temperature) as TextView
        temperatureSetPoint = context.findViewById(R.id.temperatureSetPoint) as TextView
        tempBoost = context.findViewById(R.id.tempBoost) as TextView
        led = context.findViewById(R.id.led) as TextView
    }

    fun bind(data: VaporizerData) {
        battery.text = if (data.batteryPercentage == null) "?" else "" + data.batteryPercentage!! + "%"

        temperature.text = getFormattedTemp(settings, data.currentTemperature, true) + " / "
        temperatureSetPoint.text = getFormattedTemp(settings, data.setTemperature, true)
        tempBoost.text = "+" + getFormattedTemp(settings, data.boostTemperature, false)
        led.text = (if (data.ledPercentage == null) "?" else "" + data.ledPercentage!!) + "%"
    }
}
