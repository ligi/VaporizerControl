package org.ligi.vaporizercontrol;

import android.app.Application;

public class App extends Application{

    private VaporizerCommunicator communicator;

    @Override
    public void onCreate() {
        super.onCreate();
        communicator = new VaporizerCommunicator(this);
    }

    public VaporizerCommunicator getVaporizerCommunicator() {
        return communicator;
    }
}
