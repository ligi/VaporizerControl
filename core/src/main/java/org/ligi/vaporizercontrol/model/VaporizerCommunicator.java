package org.ligi.vaporizercontrol.model;

import android.bluetooth.BluetoothAdapter;

public interface VaporizerCommunicator {
    VaporizerData getData();

    void setBoosterTemperature(int i);

    void setLEDBrightness(int i);

    void setTemperatureSetPoint(int i);

    BluetoothAdapter getBluetooth();

    void setUpdateListener(VaporizerData.VaporizerUpdateListener dataDisplayActivity);
}
