package com.msfpocketlist.syncutil

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Public
import androidx.compose.ui.graphics.vector.ImageVector
import com.msfpocketlist.common.Constant.APIKEY
import com.msfpocketlist.localdb.CacheDao
import com.msfpocketlist.remote.ApiInterface

/** [run] downloads one dataset into the cache; false when the API answered with an error code. */
class SyncTask(val title: String, val description: String, val icon: ImageVector, val run: suspend () -> Boolean)

/** Every offline dataset, shared by the background [SyncService] and the Sync screen. */
class SyncRepository(api: ApiInterface, dao: CacheDao) {
    val tasks = listOf(
        SyncTask("All employees", "Every staff member, for offline project lists", Icons.Rounded.Groups) {
            val body = api.getAllEmployee(APIKEY)
            (body.response == 200).also { if (it) dao.insertEmployees(body.employees.orEmpty()) }
        },
        SyncTask("Projects", "Projects across every mission", Icons.Rounded.Folder) {
            val body = api.getActivePockets(APIKEY)
            (body.response == 200).also { if (it) dao.insertProjects(body.projects.orEmpty()) }
        },
        SyncTask("Emergency", "Emergency contacts", Icons.Rounded.LocalHospital) {
            val body = api.getEmergencyList(APIKEY)
            (body.response == 200).also { if (it) dao.insertEmergency(body.employees.orEmpty()) }
        },
        SyncTask("Headquarters", "MSF HQ staff directory", Icons.Rounded.Apartment) {
            val body = api.getHeadquarterList(APIKEY)
            (body.response == 200).also { if (it) dao.insertHq(body.employees.orEmpty()) }
        },
        SyncTask("Missions", "All active missions", Icons.Rounded.Public) {
            val body = api.getAllMission(APIKEY)
            (body.response == 200).also { if (it) dao.insertMissions(body.missions.orEmpty()) }
        },
    )
}
