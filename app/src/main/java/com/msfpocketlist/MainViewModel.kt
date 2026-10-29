package com.msfpocketlist

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.msfpocketlist.syncutil.SyncService
import com.msfpocketlist.util.ConnectivityObserver
import com.msfpocketlist.util.Session

class MainViewModel(
    private val app: Application,
    private val session: Session,
    private val network: ConnectivityObserver,
) : ViewModel() {
    val isLoggedIn get() = session.user?.profile != null

    /** Background sync runs at most once a day. */
    fun startDailySync() {
        if (network.isConnectedNow() && !session.syncedToday) {
            ContextCompat.startForegroundService(app, Intent(app, SyncService::class.java))
        }
    }
}
