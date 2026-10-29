package com.msfpocketlist.util

import android.content.Context
import com.google.gson.Gson
import com.msfpocketlist.data.UserInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Logged-in user and last background sync date, kept in the same pref files as earlier app versions. */
class Session(context: Context) {
    private val prefs = context.getSharedPreferences("PocketList", Context.MODE_PRIVATE)
    private val syncPrefs = context.getSharedPreferences("PocketListSync", Context.MODE_PRIVATE)

    val user: UserInfo?
        get() = prefs.getString(USER_INFO, null)?.let { Gson().fromJson(it, UserInfo::class.java) }

    fun saveUser(user: UserInfo) = prefs.edit().putString(USER_INFO, Gson().toJson(user)).apply()

    fun logout() = prefs.edit().clear().apply()

    val lastSync: String? get() = syncPrefs.getString(SYNC_INFO, null)

    val syncedToday: Boolean get() = lastSync == today()

    fun markSynced() = syncPrefs.edit().putString(SYNC_INFO, today()).apply()

    private fun today(): String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

    private companion object {
        const val USER_INFO = "data"
        const val SYNC_INFO = "sync_data"
    }
}
