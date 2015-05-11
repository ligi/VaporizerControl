package org.ligi.vaporizercontrol.model;

public class CRAFTY_UUIDS {

    private static final String BASE = "-4c45-4b43-4942-265a524f5453";

    public static final String DATA_SERVICE_UUID = craft(1);

    public static final String TEMPERATURE_CHARACTERISTIC_UUID = craft(0x11);
    public static final String TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID = craft(0x21);
    public static final String TEMPERATURE_BOOST_CHARACTERISTIC_UUID = craft(0x31);
    public static final String BATTERY_CHARACTERISTIC_UUID = craft(0x41);
    public static final String LED_CHARACTERISTIC_UUID = craft(0x51);

    public static final String META_DATA_UUID = craft(2);

    public static final String MODEL_UUID = craft(0x22);
    public static final String VERSION_UUID = craft(0x32);
    public static final String SERIAL_UUID = craft(0x52);

    public static final String MISC_DATA_UUID = craft(3);
    public static final String HOURS_OF_OP_UUID = craft(0x23);

    private static String craft(int val) {
        return String.format("%08X", val) + BASE;
    }
}
