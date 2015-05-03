package org.ligi.vaporizercontrol;

import android.app.Application;
import org.ligi.tracedroid.TraceDroid;

public class App extends Application {

    private VaporizerCommunicator communicator;
    private Settings settings;

    @Override
    public void onCreate() {
        super.onCreate();
        communicator = new VaporizerCommunicator(this);
        settings = new SharedPreferencesSettings(this);

        TraceDroid.init(this);
    }

    public VaporizerCommunicator getVaporizerCommunicator() {
        return communicator;
    }

    public Settings getSettings() {
        return settings;
    }
}
