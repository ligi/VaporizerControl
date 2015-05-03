package org.ligi.vaporizercontrol;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSettings implements WritableSettings {

    private final Context ctx;

    public SharedPreferencesSettings(final Context ctx) {
        this.ctx = ctx;
    }

    private SharedPreferences getPrefs() {
        return ctx.getSharedPreferences("settings", Activity.MODE_PRIVATE);
    }

    public int getTemperatureFormat() {
        return getPrefs().getInt("temp", TEMPERATURE_CELSIUS);
    }

    public void setTemperatureFormat(int format) {
        getPrefs().edit().putInt("temp", format).commit();
    }

    public String getAutoConnectMAC() {
        return getPrefs().getString("addr", null);
    }

    public void setAutoConnectAddr(final String addr) {
        getPrefs().edit().putString("addr", addr).commit();
    }

    public boolean isDisplayUnitWanted() {
        return getPrefs().getBoolean("unit", true);
    }

    public void shouldDisplayUnit(final boolean should) {
        getPrefs().edit().putBoolean("unit", should).commit();
    }

    public boolean isPreciseWanted() {
        return getPrefs().getBoolean("precise", false);
    }

    public void shouldBePrecise(final boolean should) {
        getPrefs().edit().putBoolean("precise", should).commit();
    }


}
