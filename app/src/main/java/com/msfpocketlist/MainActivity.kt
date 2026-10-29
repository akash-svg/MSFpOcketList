package com.msfpocketlist

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msfpocketlist.ui.auth.login.LoginScreen
import com.msfpocketlist.ui.dashboard.HomeScreen
import com.msfpocketlist.ui.employee.ContactsScreen
import com.msfpocketlist.ui.pocket.ProjectsScreen
import com.msfpocketlist.ui.profile.ProfileScreen
import com.msfpocketlist.ui.sync.SyncScreen
import com.msfpocketlist.ui.theme.setAppContent
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppContent { AppNavHost(rememberNavController()) }
    }
}

private fun NavHostController.goHome() = navigate("home") { popUpTo(0) }

@Composable
private fun AppNavHost(nav: NavHostController, main: MainViewModel = koinViewModel()) {
    NavHost(
        nav,
        startDestination = "splash",
        enterTransition = { slideInHorizontally { it / 4 } + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutHorizontally { it / 4 } + fadeOut() },
    ) {
        composable("splash") {
            SplashScreen {
                if (main.isLoggedIn) {
                    main.startDailySync()
                    nav.goHome()
                } else {
                    nav.navigate("login") { popUpTo(0) }
                }
            }
        }
        composable("login") {
            LoginScreen(onLoggedIn = { main.startDailySync(); nav.goHome() })
        }
        composable("home") {
            HomeScreen(
                onOpenMission = { id, title -> nav.navigate("projects/$id?title=${Uri.encode(title.orEmpty())}") },
                onOpenProfile = { nav.navigate("profile/$it") },
                onSync = { nav.navigate("sync") },
                onLoggedOut = { nav.navigate("login") { popUpTo(0) } },
            )
        }
        composable(
            "projects/{missionId}?title={title}",
            listOf(navArgument("missionId") { type = NavType.IntType }, navArgument("title") { defaultValue = "" }),
        ) {
            ProjectsScreen(
                onBack = nav::popBackStack,
                onOpenProject = { id, title -> nav.navigate("contacts/$id?title=${Uri.encode(title.orEmpty())}") },
            )
        }
        composable(
            "contacts/{projectId}?title={title}",
            listOf(navArgument("projectId") { type = NavType.IntType }, navArgument("title") { defaultValue = "" }),
        ) {
            ContactsScreen(
                onBack = nav::popBackStack,
                onOpenProfile = { nav.navigate("profile/$it") },
            )
        }
        composable("profile/{userId}", listOf(navArgument("userId") { type = NavType.IntType })) {
            ProfileScreen(onBack = nav::popBackStack)
        }
        composable("sync") {
            SyncScreen(onBack = nav::popBackStack)
        }
    }
}

@Composable
private fun SplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(700))
        delay(800)
        onFinished()
    }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Image(
            painterResource(R.drawable.ic_brand),
            contentDescription = "MSF",
            modifier = Modifier
                .align(Alignment.Center)
                .width(260.dp)
                .graphicsLayer {
                    alpha = progress.value
                    scaleX = 0.9f + 0.1f * progress.value
                    scaleY = scaleX
                },
        )
        Text(
            "Pocket List",
            Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 32.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
