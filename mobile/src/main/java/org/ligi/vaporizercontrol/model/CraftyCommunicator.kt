package org.ligi.vaporizercontrol.model

import android.bluetooth.*
import android.content.Context
import android.os.Looper
import android.widget.Toast
import org.ligi.vaporizercontrol.model.CRAFTY_UUIDS.*
import org.ligi.vaporizercontrol.wiring.App
import java.util.UUID

public class CraftyCommunicator(private val context: Context) : VaporizerCommunicator {

    private val bt: BluetoothAdapter?
    private var gatt: BluetoothGatt? = null
    private var service: BluetoothGattService? = null
    private var updateListener: VaporizerData.VaporizerUpdateListener? = null
    private val data = VaporizerData()

    private var batteryNotificationEnabled = false
    private var tempNotificationEnabled = false
    private val running = true

    enum class State {
        SCANNING,
        CONNECTING,
        CONNECTED,
        DISCONNECTED
    }

    private var state = State.DISCONNECTED
    private val settings: WritableSettings

    init {
        settings = (context.getApplicationContext() as App).getSettings()
        bt = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).getAdapter()
        Thread(HeartBeat()).start()
    }

    override fun isBluetoothAvailable(): Boolean {
        return bt != null
    }

    public fun destroy() {
        this.updateListener = null
        if (state == State.SCANNING && bt != null) {
            bt.stopLeScan(null)
        }

        if (gatt != null && state == State.CONNECTED) {
            gatt!!.disconnect()
        }

        state = State.DISCONNECTED
    }

    override fun setUpdateListener(updateListener: VaporizerData.VaporizerUpdateListener) {
        this.updateListener = updateListener
    }

    private fun readCharacteristic(serviceUUID: String, characteristicUUID: String): Boolean {
        if (gatt == null) {
            return false
        }

        service = gatt!!.getService(UUID.fromString(serviceUUID))

        if (service == null) {
            return false
        }

        return gatt!!.readCharacteristic(service!!.getCharacteristic(UUID.fromString(characteristicUUID)))
    }

    override fun setLEDBrightness(`val`: Int) {
        data.ledPercentage = `val`
        setValue(LED_CHARACTERISTIC_UUID, `val`)
    }

    override fun setBoosterTemperature(`val`: Int) {
        data.boostTemperature = `val`
        setValue(TEMPERATURE_BOOST_CHARACTERISTIC_UUID, `val`)
    }

    private fun setValue(uuid: String, value: Int) {
        if (gatt == null) {
            return
        }

        val service = gatt!!.getService(UUID.fromString(DATA_SERVICE_UUID))

        if (service == null) {
            return
        }

        val characteristic = service.getCharacteristic(UUID.fromString(uuid))
        characteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT16, 0)
        gatt!!.writeCharacteristic(characteristic)
        updateListener!!.onUpdate(data)
        // TODO retry on failure
    }

    override fun setTemperatureSetPoint(temperatureSetPoint: Int) {
        data.setTemperature = temperatureSetPoint
        setValue(TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID, temperatureSetPoint)
    }


    private fun readNextCharacteristic(): Boolean {

        when {
            (data.batteryPercentage == null) || (data.batteryPercentage == 0) ->
                return readCharacteristic(DATA_SERVICE_UUID, BATTERY_CHARACTERISTIC_UUID)

            (data.batteryPercentage == null) || (data.batteryPercentage == 0) ->
                return readCharacteristic(DATA_SERVICE_UUID, BATTERY_CHARACTERISTIC_UUID)

            data.currentTemperature == null ->
                return readCharacteristic(DATA_SERVICE_UUID, TEMPERATURE_CHARACTERISTIC_UUID)

            data.setTemperature == null ->
                return readCharacteristic(DATA_SERVICE_UUID, TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID)

            data.boostTemperature == null ->
                return readCharacteristic(DATA_SERVICE_UUID, TEMPERATURE_BOOST_CHARACTERISTIC_UUID)

            data.ledPercentage == null ->
                return readCharacteristic(DATA_SERVICE_UUID, LED_CHARACTERISTIC_UUID)

            data.version == null ->
                return readCharacteristic(META_DATA_UUID, VERSION_UUID)

            data.serial == null ->
                return readCharacteristic(META_DATA_UUID, SERIAL_UUID)

            data.model == null ->
                return readCharacteristic(META_DATA_UUID, MODEL_UUID)

            data.hoursOfOperation == null ->
                return readCharacteristic(MISC_DATA_UUID, HOURS_OF_OP_UUID)
        }

        if (settings.isPollingWanted()) {
            if (last_poll == BATTERY_CHARACTERISTIC_UUID) {
                last_poll = TEMPERATURE_CHARACTERISTIC_UUID
                return readCharacteristic(DATA_SERVICE_UUID, TEMPERATURE_CHARACTERISTIC_UUID)
            } else {
                last_poll = BATTERY_CHARACTERISTIC_UUID
                return readCharacteristic(DATA_SERVICE_UUID, BATTERY_CHARACTERISTIC_UUID)
            }
        } else {
            if (!batteryNotificationEnabled) {
                batteryNotificationEnabled = enableNotification(gatt, UUID.fromString(BATTERY_CHARACTERISTIC_UUID))
                return batteryNotificationEnabled;
            }

            if (!tempNotificationEnabled) {
                tempNotificationEnabled = enableNotification(gatt, UUID.fromString(TEMPERATURE_CHARACTERISTIC_UUID))
                return tempNotificationEnabled;
            }
        }

        return false
    }

    var last_poll = BATTERY_CHARACTERISTIC_UUID

    private inner class HeartBeat : Runnable {

        override fun run() {
            Looper.prepare()
            while (running) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }


                if (state == State.DISCONNECTED) {
                    connectOrStartScan()
                } else {
                    readNextCharacteristic()
                }

            }
        }
    }

    private fun connectOrStartScan() {
        if (!isBluetoothAvailable()) {
            return
        }

        if (settings.getAutoConnectMAC() != null) {
            connect(settings.getAutoConnectMAC())
        } else {
            startScan()
        }
    }


    private fun connect(addr: String) {
        state = State.CONNECTING
        settings.setAutoConnectMAC(addr)
        bt!!.getRemoteDevice(addr).connectGatt(context, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(newGatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(newGatt, status, newState)

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt = newGatt
                    newGatt!!.discoverServices()
                    state = State.CONNECTED
                }

            }

            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) {
                super.onCharacteristicRead(gatt, characteristic, status)
                characteristicChange(characteristic)
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)
                characteristicChange(characteristic)
            }
        })
    }


    private fun characteristicChange(characteristic: BluetoothGattCharacteristic) {

        when (characteristic.getUuid().toString()) {
            BATTERY_CHARACTERISTIC_UUID -> {
                data.batteryPercentage = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)
            }

            TEMPERATURE_CHARACTERISTIC_UUID -> data.currentTemperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)

            TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID -> data.setTemperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)

            TEMPERATURE_BOOST_CHARACTERISTIC_UUID -> data.boostTemperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)

            LED_CHARACTERISTIC_UUID -> data.ledPercentage = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)

            VERSION_UUID -> data.version = characteristic.getStringValue(0)

            MODEL_UUID -> data.model = characteristic.getStringValue(0)

            SERIAL_UUID -> data.serial = characteristic.getStringValue(0);

            HOURS_OF_OP_UUID -> data.hoursOfOperation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)
        }

        if (updateListener != null) {
            updateListener!!.onUpdate(data)
        }
    }

    private fun enableNotification(gatt: BluetoothGatt?, enableCharacteristicFromUUID: UUID): Boolean {
        service = gatt!!.getService(UUID.fromString(DATA_SERVICE_UUID))
        val ledChar = service!!.getCharacteristic(enableCharacteristicFromUUID)
        gatt.setCharacteristicNotification(ledChar, true)
        val descriptor = ledChar.getDescriptors().get(0)
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        if (!gatt.writeDescriptor(descriptor)) {
            Toast.makeText(context, "Could not write descriptor for notification", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun startScan() {
        state = State.SCANNING
        bt!!.startLeScan(object : BluetoothAdapter.LeScanCallback {
            override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                if (state == State.SCANNING && device.getName() != null && device.getName() == "STORZ&BICKEL") {
                    bt.stopLeScan(null)
                    connect(device.getAddress())
                }
            }
        })
    }

    override fun getData(): VaporizerData {
        return data
    }
}
