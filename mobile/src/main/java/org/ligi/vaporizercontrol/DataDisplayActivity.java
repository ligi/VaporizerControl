package org.ligi.vaporizercontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import net.steamcrafted.loadtoast.LoadToast;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;
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

        if (TraceDroid.getStackTraceFiles().length > 0) {
            TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);
        } else {
            loadToast.setText("searching crafty");
            loadToast.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getApp().getVaporizerCommunicator().isBluetoothAvailable()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DataDisplayActivity.this, "can not scan - no BT available", Toast.LENGTH_LONG).show();
                    loadToast.error();
                }
            }, 1000);
        } else {
            getApp().getVaporizerCommunicator().connectAndRegisterForUpdates(this);
        }
        onUpdate(getApp().getVaporizerCommunicator().getData());
    }

    private App getApp() {
        return (App) getApplication();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
                final Settings settings = getApp().getSettings();
                temperature.setText(TemperatureFormatter.Companion.getFormattedTemp(settings, data.currentTemperature, true) + " / ");
                temperatureSetPoint.setText(TemperatureFormatter.Companion.getFormattedTemp(settings, data.setTemperature, true));
                tempBoost.setText("+" + TemperatureFormatter.Companion.getFormattedTemp(settings, data.boostTemperature, false));
                led.setText((data.ledPercentage == null ? "?" : "" + data.ledPercentage) + "%");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
