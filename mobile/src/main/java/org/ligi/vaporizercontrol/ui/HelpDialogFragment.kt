package org.ligi.vaporizercontrol.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.help.*
import org.ligi.compat.HtmlCompat
import org.ligi.vaporizercontrol.R
import org.ligi.vaporizercontrol.wiring.App

class HelpDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.help, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = (activity.applicationContext as App).vaporizerCommunicator.data

        serial.text = data.serial.nullSafeString()
        model.text = data.model.nullSafeString()
        hours.text = data.hoursOfOperation.nullSafeString()
        version.text = data.version.nullSafeString()

        help.text = HtmlCompat.fromHtml(
                "Find out more about this app and the source-code <a href='http://github.com/ligi/VaporizerControl'>on GitHub</a><br/><br/> Happy vaping!-) ")
        help.movementMethod = LinkMovementMethod()

    }

    fun Any?.nullSafeString() = if (this == null) "N/A" else this.toString()

}
