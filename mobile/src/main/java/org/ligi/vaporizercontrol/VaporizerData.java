package org.ligi.vaporizercontrol;

public class VaporizerData {
    public int batteryPercentage;
    public int currentTemperature;
    public int setTemperature;
    public int boostTemperature;
    public int ledPercentage;

    public interface VaporizerUpdateListener {
        void onUpdate(VaporizerData data);
    }
}
