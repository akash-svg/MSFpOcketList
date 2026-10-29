package com.msfpocketlist.ui.employee

import androidx.lifecycle.SavedStateHandle
import com.msfpocketlist.common.Constant.APIKEY
import com.msfpocketlist.localdb.CacheDao
import com.msfpocketlist.remote.ApiInterface
import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.ListViewModel
import com.msfpocketlist.ui.common.toContact
import com.msfpocketlist.util.ConnectivityObserver

/** Nav arguments `projectId` and `title` arrive through [handle]. */
class ContactsViewModel(handle: SavedStateHandle, api: ApiInterface, dao: CacheDao, network: ConnectivityObserver) : ListViewModel<Contact>(
    network,
    remote = {
        api.getEmployeeOnPocket(APIKEY, handle["projectId"] ?: -1)
            .takeIf { it.response == 200 }?.employees?.map { it.toContact() }
    },
    cache = { dao.employeesOfProject(handle["projectId"] ?: -1).map { it.toContact() } },
    matches = Contact::matches,
) {
    val projectTitle: String? = handle.get<String>("title")?.ifEmpty { null }
}
