package org.ligi.vaporizercontrol.wiring

import android.app.Application
import org.ligi.tracedroid.TraceDroid
import org.ligi.vaporizercontrol.model.CraftyCommunicator
import org.ligi.vaporizercontrol.model.SharedPreferencesSettings
import org.ligi.vaporizercontrol.model.VaporizerCommunicator
import org.ligi.vaporizercontrol.model.WritableSettings

class App : Application() {

    val vaporizerCommunicator: VaporizerCommunicator by lazy { CraftyCommunicator(this, settings) }
    val settings: WritableSettings by lazy { SharedPreferencesSettings(this) }

    override fun onCreate() {
        TraceDroid.init(this)
        super.onCreate()
    }
}
