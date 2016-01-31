package org.ligi.vaporizercontrol;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;

import org.ligi.vaporizercontrol.model.CraftyCommunicator;
import org.ligi.vaporizercontrol.model.VaporizerData;
import org.ligi.vaporizercontrol.model.WritableSettings;

public class MainActivity extends Activity {


    class MyWritableSettings implements WritableSettings {

        @Override
        public void shouldDisplayUnit(boolean should) {

        }

        @Override
        public void setAutoConnectMAC(String mac) {
        }

        @Override
        public void shouldBePrecise(boolean should) {

        }

        @Override
        public void setTemperatureFormat(int format) {

        }

        @Override
        public void shouldPoll(boolean poll) {

        }

        @Override
        public int getTemperatureFormat() {
            return 0;
        }

        @Override
        public String getAutoConnectMAC() {
            return null;
        }

        @Override
        public boolean isDisplayUnitWanted() {
            return false;
        }

        @Override
        public boolean isPreciseWanted() {
            return false;
        }

        @Override
        public boolean isPollingWanted() {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MyWritableSettings settings = new MyWritableSettings();
        final CraftyCommunicator communicator = new CraftyCommunicator(this, settings);


        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                final VaporizerDataBinder vaporizerDataBinder = new VaporizerDataBinder(MainActivity.this, settings);

                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final VaporizerData data = communicator.getData();

                        vaporizerDataBinder.bind(data);

                        handler.postDelayed(this, 500);
                    }
                });
            }
        });

    }
}
