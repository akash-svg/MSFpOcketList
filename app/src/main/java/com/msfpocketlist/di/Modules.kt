package com.msfpocketlist.di

import com.msfpocketlist.MainViewModel
import com.msfpocketlist.localdb.AppDatabase
import com.msfpocketlist.localdb.createDatabase
import com.msfpocketlist.remote.createApi
import com.msfpocketlist.syncutil.SyncRepository
import com.msfpocketlist.ui.auth.login.LoginViewModel
import com.msfpocketlist.ui.dashboard.EmergencyViewModel
import com.msfpocketlist.ui.dashboard.HqViewModel
import com.msfpocketlist.ui.dashboard.MissionsViewModel
import com.msfpocketlist.ui.dashboard.account.AccountViewModel
import com.msfpocketlist.ui.employee.ContactsViewModel
import com.msfpocketlist.ui.pocket.ProjectsViewModel
import com.msfpocketlist.ui.profile.ProfileViewModel
import com.msfpocketlist.ui.sync.SyncViewModel
import com.msfpocketlist.util.AndroidConnectivityObserver
import com.msfpocketlist.util.ConnectivityObserver
import com.msfpocketlist.util.Session
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    // Factory functions, not constructors, so these use the lambda form.
    single { createApi() }
    single { createDatabase(androidContext()) }
    single { get<AppDatabase>().dao() }
    singleOf(::Session)
    singleOf(::AndroidConnectivityObserver) { bind<ConnectivityObserver>() }
    singleOf(::SyncRepository)
}

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MissionsViewModel)
    viewModelOf(::HqViewModel)
    viewModelOf(::EmergencyViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::ProjectsViewModel)
    viewModelOf(::ContactsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SyncViewModel)
}

val appModules = listOf(dataModule, presentationModule)
