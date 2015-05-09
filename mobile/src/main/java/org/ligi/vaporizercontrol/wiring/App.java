package org.ligi.vaporizercontrol.wiring;

import android.app.Application;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.vaporizercontrol.model.CraftyCommunicator;
import org.ligi.vaporizercontrol.model.SharedPreferencesSettings;
import org.ligi.vaporizercontrol.model.VaporizerCommunicator;
import org.ligi.vaporizercontrol.model.WritableSettings;

public class App extends Application {

    private VaporizerCommunicator communicator;
    private WritableSettings settings;

    @Override
    public void onCreate() {
        TraceDroid.init(this);
        settings = new SharedPreferencesSettings(this);
        communicator = new CraftyCommunicator(this);

        super.onCreate();
    }

    public VaporizerCommunicator getVaporizerCommunicator() {
        return communicator;
    }

    public WritableSettings getSettings() {
        return settings;
    }
}
