package org.ligi.vaporizercontrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;
import net.steamcrafted.loadtoast.LoadToast;

import org.ligi.tracedroid.sending.TraceDroidEmailSender;
import org.ligi.vaporizercontrol.R;
import org.ligi.vaporizercontrol.VaporizerDataBinder;
import org.ligi.vaporizercontrol.model.VaporizerData;
import org.ligi.vaporizercontrol.wiring.App;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DataDisplayActivity extends AppCompatActivity implements VaporizerData.VaporizerUpdateListener {

    @Bind(R.id.intro_text)
    TextView introText;

    @Bind(R.id.dataContainer)
    ViewGroup dataContainer;

    @Bind(R.id.fam)
    FloatingActionsMenu fam;

    private LoadToast loadToast;
    private VaporizerDataBinder vaporizerDataBinder;

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
        ButterKnife.bind(this);

        introText.setText(Html.fromHtml(getString(R.string.intro_text)));
        introText.setMovementMethod(new LinkMovementMethod());

        vaporizerDataBinder = new VaporizerDataBinder(this, getApp().getSettings());

        AppRate.with(this).retryPolicy(RetryPolicy.EXPONENTIAL).initialLaunchCount(5).checkAndShow();

        loadToast = new LoadToast(this);

        if (!TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this) && !getApp().getVaporizerCommunicator().getData().hasData()) {
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
            getApp().getVaporizerCommunicator().setUpdateListener(this);
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
            case R.id.action_help:
                new AlertDialog.Builder(this).setView(new HelpViewProvider().getView(this)).setTitle("Information").setPositiveButton("OK", null).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(final VaporizerData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data.hasData()) {
                    if (introText.getVisibility() == VISIBLE) {
                        dataContainer.setVisibility(VISIBLE);
                        introText.setVisibility(GONE);
                        loadToast.success();
                    }

                    vaporizerDataBinder.bind(data);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
