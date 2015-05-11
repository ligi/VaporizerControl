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

    @Override
    public int getTemperatureFormat() {
        return getPrefs().getInt("temp", TEMPERATURE_CELSIUS);
    }

    @Override
    public void setTemperatureFormat(int format) {
        getPrefs().edit().putInt("temp", format).commit();
    }

    @Override
    public String getAutoConnectMAC() {
        return getPrefs().getString("addr", null);
    }

    @Override
    public void setAutoConnectMAC(final String mac) {
        getPrefs().edit().putString("addr", mac).commit();
    }

    @Override
    public boolean isDisplayUnitWanted() {
        return getPrefs().getBoolean("unit", true);
    }

    @Override
    public void shouldDisplayUnit(final boolean should) {
        getPrefs().edit().putBoolean("unit", should).commit();
    }

    @Override
    public boolean isPreciseWanted() {
        return getPrefs().getBoolean("precise", false);
    }

    @Override
    public boolean isPollingWanted() {
        return getPrefs().getBoolean("polling", false);
    }

    @Override
    public void shouldPoll(final boolean should) {
        getPrefs().edit().putBoolean("polling", should).commit();
    }

    @Override
    public void shouldBePrecise(final boolean should) {
        getPrefs().edit().putBoolean("precise", should).commit();
    }

}
