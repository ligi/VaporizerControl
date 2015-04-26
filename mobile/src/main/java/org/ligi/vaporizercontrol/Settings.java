package org.ligi.vaporizercontrol;

import android.content.Context;

public class Settings {
    public final static int TEMPERATURE_CELSIUS=0;
    public final static int TEMPERATURE_FAHRENHEIT=1;

    public Settings(final Context app) {

    }

    public int getTemperatureFormat() {
        return TEMPERATURE_CELSIUS;
    }
}
