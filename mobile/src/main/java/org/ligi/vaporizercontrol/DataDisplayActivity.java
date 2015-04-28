package org.ligi.vaporizercontrol;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import net.steamcrafted.loadtoast.LoadToast;
import org.ligi.vaporizercontrol.util.TemperatureFormatter;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class DataDisplayActivity extends AppCompatActivity implements VaporizerData.VaporizerUpdateListener {

    @InjectView(R.id.intro_text)
    TextView introText;

    private LoadToast loadToast;

    @OnClick(R.id.led)
    void ledClick() {
        final boolean isUnknownOrNotBright = getApp().getVaporizerCommunicator().getData().ledPercentage == null ||
                                             getApp().getVaporizerCommunicator().getData().ledPercentage == 0;
        getApp().getVaporizerCommunicator().setLEDBrightness(isUnknownOrNotBright ? 100 : 0);
    }


    @OnLongClick(R.id.led)
    boolean onLEDLongClick() {
        ChangeDialogs.showLEDPercentageDialog(this, getApp().getVaporizerCommunicator());
        return true;
    }

    @OnClick({R.id.temperature, R.id.temperatureSetPoint, R.id.tempBoost})
    void onTemperatureClick() {
        ChangeDialogs.showTemperatureDialog(this, getApp().getVaporizerCommunicator());
    }

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

    @InjectView(R.id.fam)
    FloatingActionsMenu fam;

    @OnClick(R.id.fab)
    void onFAMClick() {
        fam.toggle();
    }

    @OnClick(R.id.fab_action_edit_boost)
    void editBoostClick() {
        ChangeDialogs.setBooster(this, getApp().getVaporizerCommunicator());
        fam.collapse();
    }

    @OnClick(R.id.fab_action_edit_settemp)
    void editSetTempClick() {
        ChangeDialogs.showTemperatureDialog(this, getApp().getVaporizerCommunicator());
        fam.collapse();
    }


    @OnClick(R.id.fab_action_edit_led)
    void editLEDClick() {
        ChangeDialogs.showLEDPercentageDialog(this, getApp().getVaporizerCommunicator());
        fam.collapse();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        loadToast = new LoadToast(this);
        loadToast.setText("searching crafty");
        loadToast.show();
    }

    @Override
    protected void onPause() {
        getApp().getVaporizerCommunicator().destroy();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getApp().getVaporizerCommunicator().isBluetoothAvailable()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadToast.error();
                }
            },1000);
        } else {
            getApp().getVaporizerCommunicator().connectAndRegisterForUpdates(this);
        }
        onUpdate(getApp().getVaporizerCommunicator().getData());
    }

    private App getApp() {
        return (App) getApplication();
    }

    @Override
    public void onUpdate(final VaporizerData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (introText.getVisibility() == VISIBLE) {
                    if (data.hasData()) {
                        introText.setVisibility(GONE);
                        loadToast.success();
                    } else {
                        introText.setText(Html.fromHtml(getString(R.string.intro_text)));
                        introText.setMovementMethod(new LinkMovementMethod());
                    }
                }

                battery.setText((data.batteryPercentage == null ? "?" : "" + data.batteryPercentage) + "%");
                temperature.setText(getFormattedTemp(data.currentTemperature) + " / ");
                temperatureSetPoint.setText(getFormattedTemp(data.setTemperature));
                tempBoost.setText("+" + getFormattedTemp(data.boostTemperature));
                led.setText((data.ledPercentage == null ? "?" : "" + data.ledPercentage) + "%");
            }
        });
    }

    private String getFormattedTemp(Integer temp) {
        return TemperatureFormatter.getFormattedTemp(temp, getApp().getSettings().getTemperatureFormat());
    }
}
