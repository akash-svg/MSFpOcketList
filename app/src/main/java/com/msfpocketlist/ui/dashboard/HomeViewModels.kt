package com.msfpocketlist.ui.dashboard

import com.msfpocketlist.common.Constant.APIKEY
import com.msfpocketlist.data.Mission
import com.msfpocketlist.localdb.CacheDao
import com.msfpocketlist.remote.ApiInterface
import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.ListViewModel
import com.msfpocketlist.ui.common.toContact
import com.msfpocketlist.util.ConnectivityObserver

class MissionsViewModel(api: ApiInterface, dao: CacheDao, network: ConnectivityObserver) : ListViewModel<Mission>(
    network,
    remote = { api.getAllMission(APIKEY).takeIf { it.response == 200 }?.missions },
    cache = { dao.missions() },
    matches = { m, q -> m.title?.contains(q, true) == true },
)

class HqViewModel(api: ApiInterface, dao: CacheDao, network: ConnectivityObserver) : ListViewModel<Contact>(
    network,
    remote = { api.getHeadquarterList(APIKEY).takeIf { it.response == 200 }?.employees?.map { it.toContact() } },
    cache = { dao.hqEmployees().map { it.toContact() } },
    matches = Contact::matches,
)

class EmergencyViewModel(api: ApiInterface, dao: CacheDao, network: ConnectivityObserver) : ListViewModel<Contact>(
    network,
    remote = { api.getEmergencyList(APIKEY).takeIf { it.response == 200 }?.employees?.map { it.toContact() } },
    cache = { dao.emergencyEmployees().map { it.toContact() } },
    matches = Contact::matches,
)
