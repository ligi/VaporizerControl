package org.ligi.vaporizercontrol

import android.app.Activity
import android.view.View
import android.widget.TextView
import org.ligi.vcc.core.R
import org.ligi.vaporizercontrol.model.Settings
import org.ligi.vaporizercontrol.model.VaporizerData
import org.ligi.vaporizercontrol.util.TemperatureFormatter.getFormattedTemp

class VaporizerDataBinder(context: Activity, private val settings: Settings) {

    internal var battery: TextView
    internal var temperature: TextView
    internal var temperatureSetPoint: TextView
    internal var tempBoost: TextView
    internal var led: TextView
    internal var visibility_indicator: View

    init {
        battery = context.findViewById(R.id.battery) as TextView
        temperature = context.findViewById(R.id.temperature) as TextView
        temperatureSetPoint = context.findViewById(R.id.temperatureSetPoint) as TextView
        tempBoost = context.findViewById(R.id.tempBoost) as TextView
        led = context.findViewById(R.id.led) as TextView
        visibility_indicator = context.findViewById(R.id.visibility_indicator)
    }

    fun bind(data: VaporizerData) {
        battery.text = if (data.batteryPercentage == null) "?" else "" + data.batteryPercentage!! + "%"

        temperature.text = getFormattedTemp(settings, data.currentTemperature, true) + " / "
        temperatureSetPoint.text = getFormattedTemp(settings, data.setTemperature, true)
        tempBoost.text = "+" + getFormattedTemp(settings, data.boostTemperature, false)
        led.text = (if (data.ledPercentage == null) "?" else "" + data.ledPercentage!!) + "%"

        val isNotSeenForSomeTime = System.currentTimeMillis().minus(data.lastDataMillis?:0)>1000
        visibility_indicator.visibility = if (isNotSeenForSomeTime) View.VISIBLE else View.INVISIBLE
    }
}
