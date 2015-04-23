package org.ligi.vaporizercontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements VaporizerData.VaporizerUpdateListener {

    @InjectView(R.id.battery)
    TextView battery;

    @InjectView(R.id.temperature)
    TextView temperature;

    @InjectView(R.id.temperatureSetPoint)
    TextView temperatureSetPoint;

    @InjectView(R.id.tempBoost)
    TextView tempBoost;

    @InjectView(R.id.led)
    TextView led;

    VaporizerCommunicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        communicator=new VaporizerCommunicator(this);
    }

    @Override
    protected void onPause() {
        communicator.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        communicator.onResume(this);
        super.onResume();
    }

    @Override
    public void onUpdate(final VaporizerData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                battery.setText("" + data.batteryPercentage + "%");
                temperature.setText("" + data.currentTemperature / 10f + "° / ");
                temperatureSetPoint.setText("" + data.setTemperature / 10f + "°");
                tempBoost.setText("+" + data.boostTemperature / 10f + "°");
                led.setText("" + data.ledPercentage + "%");
            }
        });
    }
}
