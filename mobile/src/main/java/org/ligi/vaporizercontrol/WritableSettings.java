package org.ligi.vaporizercontrol;

public interface WritableSettings extends Settings{

    void shouldDisplayUnit(final boolean should);
    void setAutoConnectAddr(final String addr);
    void shouldBePrecise(final boolean should);
    void setTemperatureFormat(int format);

}
