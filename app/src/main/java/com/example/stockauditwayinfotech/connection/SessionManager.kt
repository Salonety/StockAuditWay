package com.StockTaking.connection
import android.content.Context
import android.preference.PreferenceManager

class SessionManager(context: Context?) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val REFNO = "refno"
        private const val ID = "userId"
        private const val USERNAME = "username"
        private const val NAME = "name"
        private const val COMPANYID = "companyId"
        private const val STOREID = "storeId"
        private const val ADDRESS = "address"
        private const val USERPASSWORD = "userpassword"
        private const val ISLOGIN = "islogin"
        private const val DATE = "date"
        private const val USER_ROLE = "companyName"
    }

    var isLogin: Boolean
        get() = prefs.getBoolean(ISLOGIN, false)
        set(isLogin) {
            prefs.edit().putBoolean(ISLOGIN, isLogin).apply()
        }

    fun logout() {
        prefs.edit().clear().apply()
    }

    var refNo: String?
        get() = prefs.getString(REFNO, "")
        set(refNo) {
            prefs.edit().putString(REFNO, refNo).apply()
        }

    var userId: Int
        get() = prefs.getInt(ID, 0)
        set(userId) {
            prefs.edit().putInt(ID, userId).apply()
        }
    var userName: String?
        get() = prefs.getString(USERNAME, "")
        set(userName) {
            prefs.edit().putString(USERNAME, userName).apply()
        }
    var name: String?
        get() = prefs.getString(NAME, "")
        set(name) {
            prefs.edit().putString(NAME, name).apply()
        }
    var companyId: Int
        get() = prefs.getInt(COMPANYID, 0)
        set(companyId) {
            prefs.edit().putInt(COMPANYID, companyId).apply()
        }
    var storeId: Int
        get() = prefs.getInt(STOREID, 0)
        set(storeId) {
            prefs.edit().putInt(STOREID, storeId).apply()
        }
    var currentAddress: String?
        get() = prefs.getString(ADDRESS, "")
        set(currentAddress) {
            prefs.edit().putString(ADDRESS, currentAddress).apply()
        }
    var date: String?
        get() = prefs.getString(DATE, "")
        set(date) {
            prefs.edit().putString(DATE, date).apply()
        }
    var userRoleList: Set<String>?
        get() = prefs.getStringSet(USER_ROLE, null)
        set(userRoleList) {
            prefs.edit().putStringSet(USER_ROLE, userRoleList).apply()
        }

}