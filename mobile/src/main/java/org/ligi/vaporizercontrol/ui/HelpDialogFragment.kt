package org.ligi.vaporizercontrol.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.help.*
import org.ligi.vaporizercontrol.R
import org.ligi.vaporizercontrol.wiring.App

class HelpDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.help, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = (activity.applicationContext as App).vaporizerCommunicator.data

        serial.text = handleNull(data.serial)
        model.text = handleNull(data.model)
        hours.text = handleNull(data.hoursOfOperation)
        version.text = handleNull(data.version)

        help.text = Html.fromHtml(
                "Find out more about this app and the source-code <a href='http://github.com/ligi/VaporizerControl'>on GitHub</a><br/><br/> Happy vaping!-) ")
        help.movementMethod = LinkMovementMethod()

    }

    fun handleNull(bar: Any?): String {
        return if (bar == null) "N/A" else bar.toString()
    }

}
