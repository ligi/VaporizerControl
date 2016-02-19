package org.ligi.vaporizercontrol.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.*
import org.ligi.vaporizercontrol.model.Settings
import org.ligi.vaporizercontrol.wiring.App

public class SettingsActivity : AppCompatActivity() {

    fun getApp(): App {
        return applicationContext as App;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = getApp().settings
        val mac = settings.autoConnectMAC

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        verticalLayout {
            radioGroup() {
                radioButton() {
                    text = "Celsius"
                    onClick {
                        settings.temperatureFormat = Settings.TEMPERATURE_CELSIUS
                    }
                    id = Settings.TEMPERATURE_CELSIUS;
                }

                radioButton() {
                    text = "Fahrenheit"
                    onClick {
                        settings.temperatureFormat = Settings.TEMPERATURE_FAHRENHEIT
                    }
                    id = Settings.TEMPERATURE_FAHRENHEIT;
                }

                radioButton() {
                    text = "Kelvin"
                    onClick {
                        settings.temperatureFormat = Settings.TEMPERATURE_KELVIN
                    }
                    id = Settings.TEMPERATURE_KELVIN;
                }


            }.check(settings.temperatureFormat)

            if (settings.autoConnectMAC != null) {
                button("remove $mac as default") {
                    onClick {
                        visibility = View.GONE
                        settings.autoConnectMAC = null;
                        Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
                    }
                }
            }

            checkBox("display unit") {
                isChecked = settings.isDisplayUnitWanted
                onCheckedChange { compoundButton, b ->
                    settings.shouldDisplayUnit(b);
                }
            }

            checkBox("show rlly precise") {
                isChecked = settings.isPreciseWanted
                onCheckedChange { compoundButton, b ->
                    settings.shouldBePrecise(b);
                }
            }

            checkBox("poll ( only activate with old firmwares )") {
                isChecked = settings.isPollingWanted
                onCheckedChange { compoundButton, b ->
                    settings.shouldPoll(b);
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish();
        return super.onOptionsItemSelected(item)
    }
}