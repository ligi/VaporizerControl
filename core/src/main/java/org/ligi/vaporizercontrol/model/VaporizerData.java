package org.ligi.vaporizercontrol.model;

import android.support.annotation.Nullable;

public class VaporizerData {
    @Nullable
    public Integer batteryPercentage;
    @Nullable
    public Integer currentTemperature;
    @Nullable
    public Integer setTemperature;
    @Nullable
    public Integer boostTemperature;
    @Nullable
    public Integer ledPercentage;
    @Nullable
    public String model;
    @Nullable
    public String version;
    @Nullable
    public String serial;
    @Nullable
    public Integer hoursOfOperation;
    @Nullable
    public Long lastDataMillis;

    public interface VaporizerUpdateListener {
        void onUpdate(VaporizerData data);
    }

    public boolean hasData() {
        return batteryPercentage != null || currentTemperature != null || setTemperature != null || boostTemperature != null || ledPercentage != null;
    }
}
