package org.ligi.vaporizercontrol

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.wearable.view.WatchViewStub

import org.ligi.vaporizercontrol.model.CraftyCommunicator
import org.ligi.vaporizercontrol.model.VaporizerData
import org.ligi.vaporizercontrol.model.WritableSettings

class MainActivity : Activity() {


    internal inner class MyWritableSettings : WritableSettings {

        override fun shouldDisplayUnit(should: Boolean) {

        }

        override fun setAutoConnectMAC(mac: String) {
        }

        override fun shouldBePrecise(should: Boolean) {

        }

        override fun setTemperatureFormat(format: Int) {

        }

        override fun shouldPoll(poll: Boolean) {

        }

        override fun getTemperatureFormat(): Int {
            return 0
        }

        override fun getAutoConnectMAC(): String? {
            return null
        }

        override fun isDisplayUnitWanted(): Boolean {
            return false
        }

        override fun isPreciseWanted(): Boolean {
            return false
        }

        override fun isPollingWanted(): Boolean {
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = MyWritableSettings()
        val communicator = CraftyCommunicator(this, settings)

        setContentView(R.layout.activity_main)

        val stub = findViewById(R.id.watch_view_stub) as WatchViewStub
        stub.setOnLayoutInflatedListener {
            val vaporizerDataBinder = VaporizerDataBinder(this@MainActivity, settings)

            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    val data = communicator.data

                    vaporizerDataBinder.bind(data)

                    handler.postDelayed(this, 500)
                }
            })
        }

    }
}
