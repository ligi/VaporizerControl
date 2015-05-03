package org.ligi.vaporizercontrol;

import android.app.Application;
import org.ligi.tracedroid.TraceDroid;

public class App extends Application {

    private VaporizerCommunicator communicator;
    private Settings settings;

    @Override
    public void onCreate() {
        TraceDroid.init(this);
        settings = new SharedPreferencesSettings(this);
        communicator = new VaporizerCommunicator(this);

        super.onCreate();
    }

    public VaporizerCommunicator getVaporizerCommunicator() {
        return communicator;
    }

    public Settings getSettings() {
        return settings;
    }
}
