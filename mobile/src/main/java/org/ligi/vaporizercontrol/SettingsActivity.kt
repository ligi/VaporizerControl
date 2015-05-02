package org.ligi.vaporizercontrol

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.*


public class SettingsActivity : AppCompatActivity() {

    fun getApp(): App {
        return getApplicationContext() as App;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = getApp().getSettings()
        val mac = settings.getAutoConnectMAC()

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        verticalLayout {
            radioGroup() {
                radioButton() {
                    setText("Celsius")
                    onClick {
                        settings.setTemperatureFormat(Settings.TEMPERATURE_CELSIUS)
                    }
                    id = Settings.TEMPERATURE_CELSIUS;
                }

                radioButton() {
                    setText("Fahrenheit")
                    onClick {
                        settings.setTemperatureFormat(Settings.TEMPERATURE_FAHRENHEIT)
                    }
                    id = Settings.TEMPERATURE_FAHRENHEIT;
                }

                radioButton() {
                    setText("Kelvin")
                    onClick {
                        settings.setTemperatureFormat(Settings.TEMPERATURE_KELVIN)
                    }
                    id = Settings.TEMPERATURE_KELVIN;
                }


            }.check(settings.getTemperatureFormat())

            if (settings.getAutoConnectMAC() != null) {
                button("remove " + mac + " as default") {
                    onClick {
                        setVisibility(View.GONE)
                        settings.setAutoConnectAddr(null);
                        Toast.makeText(getContext(), "done", Toast.LENGTH_LONG).show();
                    }
                }
            }

            checkBox("display unit") {
                setChecked(settings.isDisplayUnitWanted())
                onCheckedChange { compoundButton, b ->
                    settings.shouldDisplayUnit(b);
                }
            }

            checkBox("show rlly precise"){
                setChecked(settings.isPreciseWanted())
                onCheckedChange { compoundButton, b ->
                    settings.shouldBePrecise(b);
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish();
        return super.onOptionsItemSelected(item)
    }
}
