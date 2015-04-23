package org.ligi.vaporizercontrol;

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

    public interface VaporizerUpdateListener {
        void onUpdate(VaporizerData data);
    }
}
