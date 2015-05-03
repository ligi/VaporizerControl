package org.ligi.vaporizercontrol;

public interface Settings {
    int TEMPERATURE_CELSIUS = 0;
    int TEMPERATURE_FAHRENHEIT = 1;
    int TEMPERATURE_KELVIN = 2;

    int getTemperatureFormat();
    String getAutoConnectMAC();
    boolean isDisplayUnitWanted();
    boolean isPreciseWanted();

    void shouldDisplayUnit(final boolean should);
    void setAutoConnectAddr(final String addr);
    void shouldBePrecise(final boolean should);
    void setTemperatureFormat(int format);


}
