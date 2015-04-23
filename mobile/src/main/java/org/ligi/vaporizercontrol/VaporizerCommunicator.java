package org.ligi.vaporizercontrol;

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
import android.content.SharedPreferences;
import android.widget.Toast;
import java.util.UUID;

public class VaporizerCommunicator {


    public static final String SERVICE_UUID = "00000001-4c45-4b43-4942-265a524f5453";
    public static final String TEMPERATURE_CHARACTERISTIC_UUID = "00000011-4c45-4b43-4942-265a524f5453";
    public static final String TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID = "00000021-4c45-4b43-4942-265a524f5453";
    public static final String TEMPERATURE_BOOST_CHARACTERISTIC_UUID = "00000031-4c45-4b43-4942-265a524f5453";
    public static final String BATTERY_CHARACTERISTIC_UUID = "00000041-4c45-4b43-4942-265a524f5453";
    public static final String LED_CHARACTERISTIC_UUID = "00000051-4c45-4b43-4942-265a524f5453";

    private final BluetoothAdapter bt;
    private BluetoothGatt gatt;
    private BluetoothGattService service;

    private final Context context;
    private VaporizerData.VaporizerUpdateListener updateListener;
    private VaporizerData data = new VaporizerData();


    enum State {
        STATE_SCANNING,
        STATE_CONNECTING
    }

    private State state = State.STATE_SCANNING;

    public VaporizerCommunicator(final Context context) {
        this.context = context;
        bt = ((BluetoothManager) context.getSystemService(Activity.BLUETOOTH_SERVICE)).getAdapter();
    }

    public void onPause() {
        this.updateListener = null;
        if (state.equals(State.STATE_SCANNING)) {
            bt.stopLeScan(null);
        }

        if (gatt != null) {
            gatt.disconnect();
        }

    }

    public void onResume(VaporizerData.VaporizerUpdateListener updateListener) {
        this.updateListener = updateListener;
        if (getAutoConnectMAC() != null) {
            connect(getAutoConnectMAC());
        } else {
            startScan();
        }
    }


    private String getAutoConnectMAC() {
        return getPrefs().getString("addr", null);
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences("addr", Activity.MODE_PRIVATE);
    }

    private void readCharacteristic(final String uuid) {
        gatt.readCharacteristic(service.getCharacteristic(UUID.fromString(uuid)));
    }

    private void connect(String addr) {
        state = State.STATE_CONNECTING;
        getPrefs().edit().putString("addr", addr).commit();
        bt.getRemoteDevice(addr).connectGatt(context, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt newGatt, final int status, final int newState) {
                super.onConnectionStateChange(newGatt, status, newState);
                gatt = newGatt;
                newGatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                super.onServicesDiscovered(gatt, status);
                final BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));

                BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(BATTERY_CHARACTERISTIC_UUID));
                gatt.readCharacteristic(characteristic);
            }

            @Override
            public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                service = characteristic.getService();

                switch (characteristic.getUuid().toString()) {

                    case TEMPERATURE_CHARACTERISTIC_UUID:
                        readCharacteristic(TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID);
                        break;

                    case TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID:
                        readCharacteristic(TEMPERATURE_BOOST_CHARACTERISTIC_UUID);
                        break;

                    case TEMPERATURE_BOOST_CHARACTERISTIC_UUID:
                        readCharacteristic(LED_CHARACTERISTIC_UUID);
                        break;

                    case LED_CHARACTERISTIC_UUID:
                        readCharacteristic(TEMPERATURE_CHARACTERISTIC_UUID);
                        break;

                }
                characteristicChange(characteristic);
            }

            @Override
            public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                enableNotification(gatt, UUID.fromString(BATTERY_CHARACTERISTIC_UUID));
            }

            @Override
            public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                characteristicChange(characteristic);
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

    private void enableNotification(final BluetoothGatt gatt, final UUID enableCharacteristicFromUUID) {
        final BluetoothGattCharacteristic ledChar = service.getCharacteristic(enableCharacteristicFromUUID);
        gatt.setCharacteristicNotification(ledChar, true);
        BluetoothGattDescriptor descriptor = ledChar.getDescriptors().get(0);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!gatt.writeDescriptor(descriptor)) {
            Toast.makeText(context, "Could not write descriptor for notification", Toast.LENGTH_LONG).show();
        }
    }

    private void startScan() {
        bt.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                if (state.equals(State.STATE_SCANNING) && device.getName() != null && device.getName().equals("STORZ&BICKEL")) {
                    bt.stopLeScan(null);
                    connect(device.getAddress());
                }
            }
        });
    }

}
