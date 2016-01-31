package org.ligi.vaporizercontrol;

import android.app.Activity;
import android.widget.TextView;

import org.ligi.vaporizercontrol.model.Settings;
import org.ligi.vaporizercontrol.model.VaporizerData;
import org.ligi.vaporizercontrol.util.TemperatureFormatter;

import ligi.org.core.R;

public class VaporizerDataBinder {

    TextView battery;
    TextView temperature;
    TextView temperatureSetPoint;
    TextView tempBoost;
    TextView led;

    private final Settings settings;

    public VaporizerDataBinder(final Activity context, Settings settings) {
        this.settings = settings;
        battery = (TextView) context.findViewById(R.id.battery);
        temperature = (TextView) context.findViewById(R.id.temperature);
        temperatureSetPoint = (TextView) context.findViewById(R.id.temperatureSetPoint);
        tempBoost = (TextView) context.findViewById(R.id.tempBoost);
        led = (TextView) context.findViewById(R.id.led);
    }

    public void bind(VaporizerData data) {

        battery.setText((data.batteryPercentage == null ? "?" : "" + data.batteryPercentage) + "%");

        temperature.setText(TemperatureFormatter.INSTANCE.getFormattedTemp(settings, data.currentTemperature, true) + " / ");
        temperatureSetPoint.setText(TemperatureFormatter.INSTANCE.getFormattedTemp(settings, data.setTemperature, true));
        tempBoost.setText("+" + TemperatureFormatter.INSTANCE.getFormattedTemp(settings, data.boostTemperature, false));
        led.setText((data.ledPercentage == null ? "?" : "" + data.ledPercentage) + "%");

    }
}
