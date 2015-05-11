package org.ligi.vaporizercontrol.model;

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

    public void setAutoConnectMAC(final String mac) {
        getPrefs().edit().putString("addr", mac).commit();
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

    @Override
    public boolean isPollingWanted() {
        return getPrefs().getBoolean("polling", false);
    }

    public void shouldPoll(final boolean should) {
        getPrefs().edit().putBoolean("polling", should).commit();
    }

    public void shouldBePrecise(final boolean should) {
        getPrefs().edit().putBoolean("precise", should).commit();
    }

}
