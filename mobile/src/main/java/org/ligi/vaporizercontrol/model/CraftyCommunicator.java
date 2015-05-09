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
import android.content.Context;
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
    }

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

    public void connectAndRegisterForUpdates(VaporizerData.VaporizerUpdateListener updateListener) {
        this.updateListener = updateListener;
        if (state == State.DISCONNECTED) {
            if (settings.getAutoConnectMAC() != null) {
                connect(settings.getAutoConnectMAC());
            } else {
                startScan();
            }
        }
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

    public void setLEDBrightness(int val) {
        data.ledPercentage = val;
        setValue(LED_CHARACTERISTIC_UUID, val);
    }

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

        if (!batteryNotificationEnabled) {
            return batteryNotificationEnabled = enableNotification(gatt, UUID.fromString(BATTERY_CHARACTERISTIC_UUID));
        }

        if (!tempNotificationEnabled) {
            return tempNotificationEnabled = enableNotification(gatt, UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID));
        }

        return true;
    }


    private void connect(String addr) {
        state = State.CONNECTING;
        settings.setAutoConnectAddr(addr);
        bt.getRemoteDevice(addr).connectGatt(context, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt newGatt, final int status, final int newState) {
                super.onConnectionStateChange(newGatt, status, newState);
                gatt = newGatt;
                newGatt.discoverServices();
                state = State.CONNECTED;
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                super.onServicesDiscovered(gatt, status);
                readNextCharacteristic();
            }

            @Override
            public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                characteristicChange(characteristic);
                readNextCharacteristic();
            }

            @Override
            public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                readNextCharacteristic();
            }

            @Override
            public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                characteristicChange(characteristic);
                readNextCharacteristic();
            }
        });
    }


    private void characteristicChange(final BluetoothGattCharacteristic characteristic) {

        switch (characteristic.getUuid().toString()) {
            case BATTERY_CHARACTERISTIC_UUID:
                data.batteryPercentage = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                break;

            case TEMPERATURE_CHARACTERISTIC_UUID:
                data.currentTemperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                break;

            case TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID:
                data.setTemperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                break;

            case TEMPERATURE_BOOST_CHARACTERISTIC_UUID:
                data.boostTemperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                break;

            case LED_CHARACTERISTIC_UUID:
                data.ledPercentage = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
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

    public VaporizerData getData() {
        return data;
    }
}
