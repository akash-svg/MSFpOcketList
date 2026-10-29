package com.msfpocketlist.ui.profile

import androidx.lifecycle.SavedStateHandle
import com.msfpocketlist.common.Constant.APIKEY
import com.msfpocketlist.localdb.CacheDao
import com.msfpocketlist.remote.ApiInterface
import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.ListViewModel
import com.msfpocketlist.ui.common.toContact
import com.msfpocketlist.util.ConnectivityObserver

/** A one-item list, so profiles get the same network/cache/offline handling as every list. */
class ProfileViewModel(handle: SavedStateHandle, api: ApiInterface, dao: CacheDao, network: ConnectivityObserver) : ListViewModel<Contact>(
    network,
    remote = {
        api.getDetailById(APIKEY, handle["userId"] ?: -1)
            .takeIf { it.response == 200 }?.profile?.let { listOf(it.toContact()) }
    },
    cache = { listOfNotNull(dao.employee(handle["userId"] ?: -1)?.toContact()) },
)
