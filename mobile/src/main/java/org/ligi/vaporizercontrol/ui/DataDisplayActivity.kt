package org.ligi.vaporizercontrol.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Toast
import fr.nicolaspomepuy.discreetapprate.AppRate
import fr.nicolaspomepuy.discreetapprate.RetryPolicy
import kotlinx.android.synthetic.main.activity_main.*
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.compat.HtmlCompat
import org.ligi.tracedroid.sending.TraceDroidEmailSender
import org.ligi.vaporizercontrol.R
import org.ligi.vaporizercontrol.VaporizerDataBinder
import org.ligi.vaporizercontrol.model.VaporizerData
import org.ligi.vaporizercontrol.wiring.App

class DataDisplayActivity : AppCompatActivity(), VaporizerData.VaporizerUpdateListener {

    private val loadToast by lazy { LoadToast(this) }
    private val vaporizerDataBinder by lazy { VaporizerDataBinder(this, app.settings) }
    private val app by lazy { application as App }

    private var lastNightMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById(R.id.led)!!.setOnClickListener {
            val isUnknownOrNotBright = app.vaporizerCommunicator.data.ledPercentage == null || app.vaporizerCommunicator.data.ledPercentage === 0
            app.vaporizerCommunicator.setLEDBrightness(if (isUnknownOrNotBright) 100 else 0)
        }

        findViewById(R.id.led)!!.setOnLongClickListener {
            ChangeDialogs.showLEDPercentageDialog(this, app.vaporizerCommunicator)
            true
        }

        fab.setOnClickListener { fam.toggle() }

        findViewById(R.id.tempBoost)!!.setOnClickListener {
            ChangeDialogs.setBooster(this, app.vaporizerCommunicator)
        }

        fab_action_edit_boost.setOnClickListener {
            ChangeDialogs.setBooster(this, app.vaporizerCommunicator)
            fam.collapse()
        }

        fab_action_edit_settemp.setOnClickListener {
            ChangeDialogs.showTemperatureDialog(this, app.vaporizerCommunicator)
            fam.collapse()
        }

        fab_action_edit_led.setOnClickListener {
            ChangeDialogs.showLEDPercentageDialog(this, app.vaporizerCommunicator)
            fam.collapse()
        }

        findViewById(R.id.temperature)!!.setOnClickListener {
            ChangeDialogs.showTemperatureDialog(this, app.vaporizerCommunicator)
        }

        findViewById(R.id.temperatureSetPoint)!!.setOnClickListener {
            ChangeDialogs.showTemperatureDialog(this, app.vaporizerCommunicator)
        }

        intro_text.text = HtmlCompat.fromHtml(getString(R.string.intro_text))
        intro_text.movementMethod = LinkMovementMethod()

        AppRate.with(this).retryPolicy(RetryPolicy.EXPONENTIAL).initialLaunchCount(5).checkAndShow()

        if (!TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this) && !app.vaporizerCommunicator.data.hasData()) {
            loadToast.setText("searching crafty")
            loadToast.show()
        }
    }


    override fun onResume() {
        super.onResume()
        val bluetooth = app.vaporizerCommunicator.bluetooth
        if (bluetooth == null) {
            Handler().postDelayed({
                Toast.makeText(this@DataDisplayActivity, "can not scan - no BT available", Toast.LENGTH_LONG).show()
                loadToast.error()
            }, 1000)
        } else {
            if (!bluetooth.isEnabled) {
                bluetooth.enable()
            }
            app.vaporizerCommunicator.setUpdateListener(this)
        }
        onUpdate(app.vaporizerCommunicator.data)

        if (lastNightMode !=null && lastNightMode !=app.settings.nightMode) {
            recreate()
        }

        lastNightMode = app.settings.nightMode
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_help -> HelpDialogFragment().show(supportFragmentManager, "help")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdate(data: VaporizerData) {
        runOnUiThread {
            if (data.hasData()) {
                if (intro_text.visibility == VISIBLE) {
                    dataContainer.visibility = VISIBLE
                    intro_text.visibility = GONE
                    loadToast.success()
                }

                vaporizerDataBinder.bind(data)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    }

}
