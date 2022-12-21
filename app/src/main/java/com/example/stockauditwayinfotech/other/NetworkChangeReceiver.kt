package com.StockTaking.other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            if (isOnline(context)) {
                dialog(true)
                Log.e("vishal", "Online Connect Intenet ")
            } else {
                dialog(false)
                Log.e("vishal", "Conectivity Failure !!! ")
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun dialog(b: Boolean) {

    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}