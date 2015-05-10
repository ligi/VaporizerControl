package org.ligi.vaporizercontrol.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;
import java.util.UUID;
import org.ligi.vaporizercontrol.wiring.App;
import static org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.BATTERY_CHARACTERISTIC_UUID;
import static org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.LED_CHARACTERISTIC_UUID;
import static org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.SERVICE_UUID;
import static org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.TEMPERATURE_BOOST_CHARACTERISTIC_UUID;
import static org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.TEMPERATURE_CHARACTERISTIC_UUID;
import static org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID;

public class CraftyCommunicator implements VaporizerCommunicator {

    private final BluetoothAdapter bt;
    private BluetoothGatt gatt;
    private BluetoothGattService service;

    private final Context context;
    private VaporizerData.VaporizerUpdateListener updateListener;
    private VaporizerData data = new VaporizerData();

    private boolean batteryNotificationEnabled = false;
    private boolean tempNotificationEnabled = false;
    private boolean running = true;

    enum State {
        SCANNING,
        CONNECTING,
        CONNECTED,
        DISCONNECTED
    }

    private State state = State.DISCONNECTED;
    private WritableSettings settings;

    public CraftyCommunicator(final Context context) {
        this.context = context;
        settings = ((App) context.getApplicationContext()).getSettings();
        bt = ((BluetoothManager) context.getSystemService(Activity.BLUETOOTH_SERVICE)).getAdapter();
        new Thread(new HeartBeat()).start();
    }

    @Override
    public boolean isBluetoothAvailable() {
        return bt != null;
    }

    public void destroy() {
        this.updateListener = null;
        if (state.equals(State.SCANNING) && bt != null) {
            bt.stopLeScan(null);
        }

        if (gatt != null && state.equals(State.CONNECTED)) {
            gatt.disconnect();
        }

        state = State.DISCONNECTED;
    }

    @Override
    public void setUpdateListener(VaporizerData.VaporizerUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    private boolean readCharacteristic(final String uuid) {
        if (gatt == null) {
            return false;
        }

        service = gatt.getService(UUID.fromString(SERVICE_UUID));

        if (service == null) {
            return false;
        }

        return gatt.readCharacteristic(service.getCharacteristic(UUID.fromString(uuid)));
    }

    @Override
    public void setLEDBrightness(int val) {
        data.ledPercentage = val;
        setValue(LED_CHARACTERISTIC_UUID, val);
    }

    @Override
    public void setBoosterTemperature(int val) {
        data.boostTemperature = val;
        setValue(TEMPERATURE_BOOST_CHARACTERISTIC_UUID, val);
    }

    private void setValue(final String uuid, final int val) {
        if (gatt == null) {
            return;
        }

        final BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));

        if (service == null) {
            return;
        }

        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(uuid));
        characteristic.setValue(val, BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        gatt.writeCharacteristic(characteristic);
        updateListener.onUpdate(data);
        // TODO retry on failure
    }

    @Override
    public void setTemperatureSetPoint(final int temperatureSetPoint) {
        data.setTemperature = temperatureSetPoint;
        setValue(TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID, temperatureSetPoint);
    }


    private boolean readNextCharacteristic() {
        if ((data.batteryPercentage == null) || (data.batteryPercentage == 0)) {
            return readCharacteristic(BATTERY_CHARACTERISTIC_UUID);
        }

        if (data.currentTemperature == null) {
            return readCharacteristic(TEMPERATURE_CHARACTERISTIC_UUID);
        }

        if (data.setTemperature == null) {
            return readCharacteristic(TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID);
        }

        if (data.boostTemperature == null) {
            return readCharacteristic(TEMPERATURE_BOOST_CHARACTERISTIC_UUID);
        }

        if (data.ledPercentage == null) {
            return readCharacteristic(LED_CHARACTERISTIC_UUID);
        }

        if (settings.isPollingWanted()) {
            if (last_poll.equals(BATTERY_CHARACTERISTIC_UUID)) {
                last_poll = TEMPERATURE_CHARACTERISTIC_UUID;
                return readCharacteristic(TEMPERATURE_CHARACTERISTIC_UUID);
            } else {
                last_poll = BATTERY_CHARACTERISTIC_UUID;
                return readCharacteristic(BATTERY_CHARACTERISTIC_UUID);
            }
        } else {
            if (!batteryNotificationEnabled) {
                return batteryNotificationEnabled = enableNotification(gatt, UUID.fromString(BATTERY_CHARACTERISTIC_UUID));
            }

            if (!tempNotificationEnabled) {
                return tempNotificationEnabled = enableNotification(gatt, UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID));
            }
        }

        return false;
    }

    String last_poll = BATTERY_CHARACTERISTIC_UUID;

    private class HeartBeat implements Runnable {

        @Override
        public void run() {
            Looper.prepare();
            while (running) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (state == State.DISCONNECTED) {
                    connectOrStartScan();
                } else {
                    readNextCharacteristic();
                }

            }
        }
    }

    private void connectOrStartScan() {
        if (!isBluetoothAvailable()) {
            return;
        }

        if (settings.getAutoConnectMAC() != null) {
            connect(settings.getAutoConnectMAC());
        } else {
            startScan();
        }
    }


    private void connect(String addr) {
        state = State.CONNECTING;
        settings.setAutoConnectAddr(addr);
        bt.getRemoteDevice(addr).connectGatt(context, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt newGatt, final int status, final int newState) {
                super.onConnectionStateChange(newGatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt = newGatt;
                    newGatt.discoverServices();
                    state = State.CONNECTED;
                }

            }
            @Override
            public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                characteristicChange(characteristic);
            }

            @Override
            public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                characteristicChange(characteristic);
            }
        });
    }


    private void characteristicChange(final BluetoothGattCharacteristic characteristic) {

        final Integer uint16val = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        switch (characteristic.getUuid().toString()) {
            case BATTERY_CHARACTERISTIC_UUID:
                data.batteryPercentage = uint16val;
                break;

            case TEMPERATURE_CHARACTERISTIC_UUID:
                data.currentTemperature = uint16val;
                break;

            case TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID:
                data.setTemperature = uint16val;
                break;

            case TEMPERATURE_BOOST_CHARACTERISTIC_UUID:
                data.boostTemperature = uint16val;
                break;

            case LED_CHARACTERISTIC_UUID:
                data.ledPercentage = uint16val;
                break;
        }

        if (updateListener != null) {
            updateListener.onUpdate(data);
        }
    }

    private boolean enableNotification(final BluetoothGatt gatt, final UUID enableCharacteristicFromUUID) {
        service = gatt.getService(UUID.fromString(SERVICE_UUID));
        final BluetoothGattCharacteristic ledChar = service.getCharacteristic(enableCharacteristicFromUUID);
        gatt.setCharacteristicNotification(ledChar, true);
        BluetoothGattDescriptor descriptor = ledChar.getDescriptors().get(0);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!gatt.writeDescriptor(descriptor)) {
            Toast.makeText(context, "Could not write descriptor for notification", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void startScan() {
        state = State.SCANNING;
        bt.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                if (state.equals(State.SCANNING) && device.getName() != null && device.getName().equals("STORZ&BICKEL")) {
                    bt.stopLeScan(null);
                    connect(device.getAddress());
                }
            }
        });
    }

    @Override
    public VaporizerData getData() {
        return data;
    }
}
