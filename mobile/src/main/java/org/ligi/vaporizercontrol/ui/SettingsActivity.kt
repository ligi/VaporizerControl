package org.ligi.vaporizercontrol.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.*
import org.ligi.vaporizercontrol.model.Settings
import org.ligi.vaporizercontrol.wiring.App

class SettingsActivity : AppCompatActivity() {

    val settings by lazy { (applicationContext as App).settings }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mac = settings.autoConnectMAC

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        verticalLayout {
            radioGroup() {
                tempFormatRadio(Settings.TEMPERATURE_CELSIUS, "Celsius")
                tempFormatRadio(Settings.TEMPERATURE_FAHRENHEIT, "Fahrenheit")
                tempFormatRadio(Settings.TEMPERATURE_KELVIN, "Kelvin")
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

            settingsCheckBox(settings.isDisplayUnitWanted, "display unit") { compoundButton, b ->
                settings.shouldDisplayUnit(b)
            }

            settingsCheckBox(settings.isDisplayUnitWanted, "show rlly precise") { compoundButton, b ->
                settings.shouldBePrecise(b)
            }

            settingsCheckBox(settings.isDisplayUnitWanted, "poll ( only activate with old firmwares )") { compoundButton, b ->
                settings.shouldPoll(b)
            }

            radioGroup() {
                dayNightRadio(AppCompatDelegate.MODE_NIGHT_YES, "Night")
                dayNightRadio(AppCompatDelegate.MODE_NIGHT_NO, "Day")
                dayNightRadio(AppCompatDelegate.MODE_NIGHT_AUTO, "Auto")
            }.check(settings.nightMode)
        }

    }

    private fun _LinearLayout.settingsCheckBox(state: Boolean, label: String, function: (Any?, Boolean) -> Unit) {
        checkBox(label) {
            isChecked = state
            onCheckedChange(function)
        }
    }

    private fun _RadioGroup.dayNightRadio(mode: Int, label: String) {
       radio(mode, label) {
           settings.nightMode = mode
           AppCompatDelegate.setDefaultNightMode(mode)
           recreate()
       }
    }

    private fun _RadioGroup.tempFormatRadio(tempFormat: Int, label: String) {
        radio(tempFormat, label) {
            settings.temperatureFormat = tempFormat
        }
    }

    private fun _RadioGroup.radio(value: Int, label: String, onClick: (View?) -> Unit) {
        radioButton() {
            text = label
            onClick(onClick)
            id = value
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}