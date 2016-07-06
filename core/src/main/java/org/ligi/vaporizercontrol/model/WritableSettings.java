package org.ligi.vaporizercontrol.model;

public interface WritableSettings extends Settings {

    void shouldDisplayUnit(final boolean should);

    void setAutoConnectMAC(final String mac);

    void shouldBePrecise(final boolean should);

    void setTemperatureFormat(int format);

    void shouldPoll(final boolean poll);

    void setNightMode(final int nightMode);

}
