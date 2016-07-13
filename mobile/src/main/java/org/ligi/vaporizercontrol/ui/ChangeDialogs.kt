package org.ligi.vaporizercontrol.ui

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.ligi.vaporizercontrol.model.VaporizerCommunicator
import org.ligi.vaporizercontrol.util.TemperatureFormatter
import org.ligi.vaporizercontrol.wiring.App

object ChangeDialogs {

    fun setBooster(ctx: Context, comm: VaporizerCommunicator) {
        val discreteSeekBar = DiscreteSeekBar(ctx)
        discreteSeekBar.max = (2100 - comm.data.setTemperature!!) / 10
        discreteSeekBar.setIndicatorFormatter("%d°")

        if (comm.data.boostTemperature != null) {
            discreteSeekBar.progress = comm.data.boostTemperature!! / 10
        }

        discreteSeekBar.setOnProgressChangeListener { discreteSeekBar, i, b -> comm.setBoosterTemperature(i * 10) }

        AlertDialog.Builder(ctx).setMessage("Set booster temperature").setView(discreteSeekBar).setPositiveButton("OK", null).show()
    }

    fun showLEDPercentageDialog(ctx: Context, comm: VaporizerCommunicator) {
        val discreteSeekBar = DiscreteSeekBar(ctx)
        discreteSeekBar.max = 100
        discreteSeekBar.setIndicatorFormatter("%d%%")

        if (comm.data.ledPercentage != null) {
            discreteSeekBar.progress = comm.data.ledPercentage!!
        }

        discreteSeekBar.setOnProgressChangeListener { discreteSeekBar, i, b -> comm.setLEDBrightness(i) }

        AlertDialog.Builder(ctx).setMessage("Set LED brightness").setView(discreteSeekBar).setPositiveButton("OK", null).show()
    }

    private var temperatureList = listOf(

            // src http://web.archive.org/web/20100223183517/http://en.wikipedia.org/wiki/Vaporizer
            TemperatureSetting(1230, "<a href='http://en.wikipedia.org/wiki/Syzygium_aromaticum'>Clove</a> Dried flower buds"),
            TemperatureSetting(1300,
                    "<a href='http://en.wikipedia.org/wiki/Eucalyptus_globulus'>Eucalyptus</a> " + "or <a href='http://en.wikipedia.org/wiki/Lavandula_angustifolia'>Lavender</a> Leaves"),
            TemperatureSetting(1400, "<a href='http://en.wikipedia.org/wiki/Ginkgo_biloba'>Ginkgo</a> Leaves or seeds"),
            TemperatureSetting(1420, "<a href='http://en.wikipedia.org/wiki/Melissa_officinalis'>Lemon balm</a> Leaves"),
            TemperatureSetting(1540, "<a href='http://en.wikipedia.org/wiki/Humulus_lupulus'>Hops</a> Cones"),
            TemperatureSetting(1830, "<a href='http://en.wikipedia.org/wiki/Aloe_vera'>Aloe_vera</a> Gelatinous fluid from leaves"),
            TemperatureSetting(1900, "<a href='http://en.wikipedia.org/wiki/Chamomilla_recutita'>Chamomile</a> Flowers " +
                    "or <a href='http://en.wikipedia.org/wiki/Salvia_officinalis'>Sage</a> Leaves " +
                    "or  <a href='http://en.wikipedia.org/wiki/Thymus_vulgaris'>Thyme</a> Herb")
            )


    fun showTemperatureDialog(ctx: Context, comm: VaporizerCommunicator) {

        val settings = (ctx.applicationContext as App).settings

        val scrollView = ScrollView(ctx)

        scrollView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val lin = LinearLayout(ctx)
        lin.orientation = LinearLayout.VERTICAL

        val discreteSeekBar = DiscreteSeekBar(ctx)
        discreteSeekBar.min = 40
        discreteSeekBar.max = 210
        discreteSeekBar.setIndicatorFormatter("%d°")

        if (comm.data.setTemperature != null) {
            discreteSeekBar.progress = comm.data.setTemperature!! / 10
        }

        lin.addView(discreteSeekBar)


        for (temperatureSetting in temperatureList) {
            val grp = RadioGroup(ctx)
            grp.orientation = LinearLayout.HORIZONTAL
            grp.setPadding(16, 16, 16, 16)
            val txt = TextView(ctx)
            txt.text = Html.fromHtml(temperatureSetting.htmlDescription)
            txt.movementMethod = LinkMovementMethod()

            val button = Button(ctx)
            button.text = TemperatureFormatter.getFormattedTemp(settings, temperatureSetting.temp, true)
            button.setOnClickListener { discreteSeekBar.progress = temperatureSetting.temp / 10 }
            grp.addView(button)
            grp.addView(txt)
            lin.addView(grp)
        }
        scrollView.addView(lin)

        AlertDialog.Builder(ctx).setView(scrollView).setTitle("Set Temperature").setPositiveButton("OK") { dialog, which -> comm.setTemperatureSetPoint(discreteSeekBar.progress * 10) }.show()
    }

    private class TemperatureSetting(val temp: Int, val htmlDescription: String)
}
