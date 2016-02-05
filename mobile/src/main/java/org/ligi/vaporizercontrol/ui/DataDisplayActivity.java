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
import android.widget.TextView;
import android.widget.Toast;

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
import snow.skittles.Skittle;
import snow.skittles.SkittleBuilder;
import snow.skittles.SkittleContainer;
import snow.skittles.SkittleLayout;
import snow.skittles.TextSkittle;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DataDisplayActivity extends AppCompatActivity implements VaporizerData.VaporizerUpdateListener {

    @Bind(R.id.intro_text)
    TextView introText;

    @Bind(R.id.skittleLayout)
    SkittleLayout fam;

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

    //foo
    void onFAMClick() {
        //foofam.toggle();
    }

    //foo
    void editBoostClick() {
        ChangeDialogs.setBooster(this, getApp().getVaporizerCommunicator());
        //foofam.collapse();
    }

    //foo
    void editSetTempClick() {
        ChangeDialogs.showTemperatureDialog(this, getApp().getVaporizerCommunicator());
        //fam.collapse();
    }


    //foo
    void editLEDClick() {
        ChangeDialogs.showLEDPercentageDialog(this, getApp().getVaporizerCommunicator());
        //foofam.collapse();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final SkittleBuilder builder = SkittleBuilder.newInstance(fam);
        builder.changeMainSkittleColor(getResources().getColor(R.color.accent));

        final TextSkittle.Builder foo = new TextSkittle.Builder("foo", getResources().getColor(R.color.accent), getResources().getDrawable(R.drawable.ic_action_bulb));

        foo.setTextBackground(getResources().getColor(R.color.accent));

        builder.addSkittle(foo.build());

//        builder.makeTextSkittle("foo",R.drawable.ic_action_bulb).add();

//        builder.addSkittle(R.drawable.ic_action_bulb, R.color.accent);

/*
        builder.setSkittleListener(new SkittleBuilder.SkittleClickListener() {
            @Override
            public void onSkittleClick(Skittle skittle) {

            }

            @Override
            public void onTextSkittleClick(TextSkittle textSkittle, String type) {

            }
        });
        builder.addSkittle(R.drawable.barratheon_icon, R.color.barratheon);
        builder.addSkittle(R.drawable.stark_icon, R.color.stark);
        */

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
                if (introText.getVisibility() == VISIBLE) {
                    if (data.hasData()) {
                        introText.setVisibility(GONE);
                        loadToast.success();
                    } else {
                        introText.setText(Html.fromHtml(getString(R.string.intro_text)));
                        introText.setMovementMethod(new LinkMovementMethod());
                    }
                }

                vaporizerDataBinder.bind(data);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
