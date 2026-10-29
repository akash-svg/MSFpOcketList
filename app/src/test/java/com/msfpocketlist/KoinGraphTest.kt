package com.msfpocketlist

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.msfpocketlist.di.appModules
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinGraphTest {
    /** Fails if any ViewModel or service asks for a type no module provides. */
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun everyDependencyIsProvided() {
        module { includes(appModules) }.verify(
            extraTypes = listOf(Application::class, Context::class, SavedStateHandle::class),
        )
    }
}
