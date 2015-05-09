package org.ligi.vaporizercontrol.model;

public interface VaporizerCommunicator {
    VaporizerData getData();

    void setBoosterTemperature(int i);

    void setLEDBrightness(int i);

    void setTemperatureSetPoint(int i);

    boolean isBluetoothAvailable();

    void setUpdateListener(VaporizerData.VaporizerUpdateListener dataDisplayActivity);
}
