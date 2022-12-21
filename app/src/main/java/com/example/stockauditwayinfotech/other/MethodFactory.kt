package com.StockTaking.other

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.stockauditwayinfotech.R
import java.lang.Exception
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

public class MethodFactory {

    companion object {

        fun myToast(activity: Activity, message: String) {
            val layout = activity.layoutInflater.inflate(
                R.layout.my_toast,
                activity.findViewById(R.id.toast_container)
            )
            val textView = layout.findViewById<TextView>(R.id.toast_text)
            textView.text = message
            val myToast = Toast(activity)
            myToast.duration = Toast.LENGTH_SHORT
            myToast.setGravity(Gravity.BOTTOM, 0, 40)
            myToast.view = layout //setting the view of custom toast layout
            myToast.show()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                            return true
                        }
                    }
                }
            }
            return false
        }

        fun getFinYear(): Int {
            val FIRST_FISCAL_MONTH: Int = Calendar.MARCH
            val month: Int = Calendar.getInstance().get(Calendar.MONTH)
            val year: Int = Calendar.getInstance().get(Calendar.YEAR)
            return if (month >= FIRST_FISCAL_MONTH) year else year - 1
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun changeDateFormat(date: String): String {
            val inf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val out = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
            return out.format(inf.parse(date));
        }

        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr: String = addr.hostAddress
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4) return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                        0,
                                        delim
                                    ).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {
            } // for now eat exceptions
            return ""
        }
    }
}
