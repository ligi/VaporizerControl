package org.ligi.vaporizercontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static final String BATTERY_CHARACTERISTIC_UUID = "00000041-4c45-4b43-4942-265a524f5453";
    public static final String TEMPERATURE_CHARACTERISTIC_UUID = "00000011-4c45-4b43-4942-265a524f5453";
    public static final String TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID = "00000021-4c45-4b43-4942-265a524f5453";
    public static final String TEMPERATURE_BOOST_CHARACTERISTIC_UUID = "00000031-4c45-4b43-4942-265a524f5453";
    public static final String LED_CHARACTERISTIC_UUID = "00000051-4c45-4b43-4942-265a524f5453";
    public static final String SERVICE_UUID = "00000001-4c45-4b43-4942-265a524f5453";

    @InjectView(R.id.battery)
    TextView battery;

    @InjectView(R.id.temperature)
    TextView temperature;

    @InjectView(R.id.temperatureSetPoint)
    TextView temperatureSetPoint;

    @InjectView(R.id.tempBoost)
    TextView tempBoost;

    @InjectView(R.id.led)
    TextView led;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        getBluetooth().startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                if (device.getName() != null && device.getName().equals("STORZ&BICKEL")) {
                    connect(device.getAddress());
                    getBluetooth().stopLeScan(null);
                }
            }
        });

    }

    private void connect(String addr) {
        getBluetooth().getRemoteDevice(addr).connectGatt(this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                gatt.discoverServices();
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

                switch (characteristic.getUuid().toString()) {
                    case BATTERY_CHARACTERISTIC_UUID:
                        gatt.readCharacteristic(characteristic.getService().getCharacteristic(UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID)));
                        break;
                    case TEMPERATURE_CHARACTERISTIC_UUID:
                        gatt.readCharacteristic(characteristic.getService().getCharacteristic(UUID.fromString(TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID)));
                        break;

                    case TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID:
                        gatt.readCharacteristic(characteristic.getService().getCharacteristic(UUID.fromString(TEMPERATURE_BOOST_CHARACTERISTIC_UUID)));
                        break;

                    case TEMPERATURE_BOOST_CHARACTERISTIC_UUID:
                        gatt.readCharacteristic(characteristic.getService().getCharacteristic(UUID.fromString(LED_CHARACTERISTIC_UUID)));
                        break;

                    case LED_CHARACTERISTIC_UUID:
                        enableNotification(gatt, characteristic);
                        break;

                }
                characteristicChange(characteristic);
            }

            @Override
            public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                characteristicChange(characteristic);
            }
        });
    }

    private void enableNotification(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        final BluetoothGattCharacteristic ledChar = characteristic.getService().getCharacteristic(UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID));
        gatt.setCharacteristicNotification(ledChar, true);
        BluetoothGattDescriptor descriptor = ledChar.getDescriptors().get(0);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!gatt.writeDescriptor(descriptor)) {
            Toast.makeText(MainActivity.this, "Could not write descriptor for notification", Toast.LENGTH_LONG).show();
        }
    }

    private void characteristicChange(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (characteristic.getUuid().toString()) {
                    case BATTERY_CHARACTERISTIC_UUID:
                        battery.setText("" + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) + "%");
                        break;
                    case TEMPERATURE_CHARACTERISTIC_UUID:
                        temperature.setText("" + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) / 10f + "° / ");
                        break;

                    case TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID:
                        temperatureSetPoint.setText("" + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) / 10f + "°");
                        break;

                    case TEMPERATURE_BOOST_CHARACTERISTIC_UUID:
                        tempBoost.setText("+" + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) / 10f + "°");
                        break;

                    case LED_CHARACTERISTIC_UUID:
                        led.setText("" + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) + "%");
                        break;


                }
            }


        });
    }

    private BluetoothAdapter getBluetooth() {
        return ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
    }

}
