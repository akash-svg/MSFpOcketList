package com.msfpocketlist.ui.pocket

import androidx.lifecycle.SavedStateHandle
import com.msfpocketlist.common.Constant.APIKEY
import com.msfpocketlist.data.Project
import com.msfpocketlist.localdb.CacheDao
import com.msfpocketlist.remote.ApiInterface
import com.msfpocketlist.ui.common.ListViewModel
import com.msfpocketlist.util.ConnectivityObserver

/** Nav arguments `missionId` and `title` arrive through [handle]. */
class ProjectsViewModel(handle: SavedStateHandle, api: ApiInterface, dao: CacheDao, network: ConnectivityObserver) : ListViewModel<Project>(
    network,
    remote = { api.getAllPocketList(APIKEY, handle["missionId"] ?: -1).takeIf { it.response == 200 }?.projects },
    cache = { dao.projects(handle["missionId"] ?: -1) },
    matches = { p, q -> p.title?.contains(q, true) == true },
) {
    val missionTitle: String? = handle.get<String>("title")?.ifEmpty { null }
}
