package frb.axeron.manager.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.ActivateScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ExecutePluginActionScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FlashScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.QuickShellScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.utils.isRouteOnBackStackAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import frb.axeron.api.AxeronInfo
import frb.axeron.manager.R
import frb.axeron.manager.ui.navigation.BottomBarDestination
import frb.axeron.manager.ui.screen.FlashIt
import frb.axeron.manager.ui.theme.AxManagerTheme
import frb.axeron.manager.ui.util.LocalSnackbarHost
import frb.axeron.manager.ui.util.LocaleHelper
import frb.axeron.manager.ui.viewmodel.ActivateViewModel
import frb.axeron.manager.ui.viewmodel.AppsViewModel
import frb.axeron.manager.ui.viewmodel.PluginViewModel
import frb.axeron.manager.ui.viewmodel.PrivilegeViewModel
import frb.axeron.manager.ui.viewmodel.SettingsViewModel
import frb.axeron.manager.ui.viewmodel.ViewModelGlobal
import frb.axeron.server.PluginInstaller

class AxActivity : ComponentActivity() {

    companion object {
        const val OPEN_QUICK_SHELL = "FolkPure.QUICK_SHELL"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    var zipUri by mutableStateOf<List<PluginInstaller>?>(emptyList())

    val shortcut by lazy {
        ShortcutInfoCompat.Builder(this, "quick-shell")
            .setShortLabel(getString(R.string.quick_shell_short))
            .setLongLabel(getString(R.string.quick_shell))
            .setDisabledMessage(getString(R.string.quick_shell_not_supported))
            .setIcon(IconCompat.createWithResource(this, R.drawable.terminal))
            .setIntent(
                Intent(this, AxActivity::class.java).apply {
                    action = OPEN_QUICK_SHELL
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            .build()
    }

    private var intentState: Intent? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ShortcutManagerCompat.pushDynamicShortcut(this@AxActivity, shortcut)

        intentState = intent

        setContent {
            AxManagerTheme {
                MainScreen(it)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentState = intent
        Log.i("AxActivity", "onNewIntent")
    }

    @Composable
    fun MainScreen(settingsViewModel: SettingsViewModel) {
        val snackBarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        val navigator = navController.rememberDestinationsNavigator()
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination

        val context = LocalContext.current

        val activateViewModel: ActivateViewModel = viewModel<ActivateViewModel>()

        val appsViewModel: AppsViewModel = viewModel<AppsViewModel>()
        val privilegeViewModel: PrivilegeViewModel = viewModel<PrivilegeViewModel>()
        val pluginViewModel: PluginViewModel = viewModel<PluginViewModel>()

        val axeronInfo = activateViewModel.axeronInfo

        LaunchedEffect(intentState) {
            if (intentState == null) return@LaunchedEffect
            Log.i("AxActivity", "intent: $intentState")
            when (intentState!!.action) {
                OPEN_QUICK_SHELL -> {
                    if (axeronInfo.isRunning()) {
                        navigator.navigate(HomeScreenDestination()) {
                            popUpTo(NavGraphs.root) {
                                saveState = true
                            }
                        }
                        if (navController.currentDestination?.route == HomeScreenDestination.route) {
                            navigator.navigate(QuickShellScreenDestination())
                        }
                    }
                }

                Intent.ACTION_VIEW -> {
                    if (intentState!!.data?.scheme == "content") {
                        zipUri =
                            intent.data?.let { uri ->
                                arrayListOf(PluginInstaller(uri))
                            } ?: run {
                                val uris: ArrayList<Uri>? =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        intent.getParcelableArrayListExtra("uris", Uri::class.java)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        intent.getParcelableArrayListExtra("uris")
                                    }

                                uris
                                    ?.map { PluginInstaller(it) }
                                    ?.toCollection(arrayListOf())
                                    ?: arrayListOf()
                            }
                        if (!zipUri.isNullOrEmpty()) {
                            navigator.navigate(
                                FlashScreenDestination(
                                    flashIt = FlashIt.FlashPlugins(zipUri!!)
                                )
                            )
                            zipUri = null
                        }
                    }
                }
            }

            intentState = null
        }

        LaunchedEffect(axeronInfo) {
            if (axeronInfo.isRunning()) {
                pluginViewModel.fetchModuleList()
                appsViewModel.loadInstalledApps()
                privilegeViewModel.loadInstalledApps()
            }
        }

        val viewModelGlobal = remember {
            ViewModelGlobal(
                settingsViewModel = settingsViewModel,
                appsViewModel = appsViewModel,
                activateViewModel = activateViewModel,
                pluginViewModel = pluginViewModel,
                privilegeViewModel = privilegeViewModel
            )
        }

        val showBottomBar = when (currentDestination?.route) {
            ActivateScreenDestination.route -> false
            FlashScreenDestination.route -> false
            ExecutePluginActionScreenDestination.route -> false
            else -> true
        }

        val bottomBarRoutes = remember {
            BottomBarDestination.entries.map { it.direction.route }.toSet()
        }

        Box {
            Scaffold(
                contentWindowInsets = WindowInsets()
            ) { innerPadding ->
                CompositionLocalProvider(
                    LocalSnackbarHost provides snackBarHostState,
                ) {
                    DestinationsNavHost(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(bottom = if (showBottomBar) 80.dp else 0.dp),
                        navGraph = NavGraphs.root,
                        navController = navController,
                        dependenciesContainerBuilder = {
                            dependency(viewModelGlobal)
                        },
                        defaultTransitions = createNavTransitions(bottomBarRoutes)
                    )
                }
            }

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomBar(
                    navController,
                    navigator,
                    activateViewModel.axeronInfo,
                    pluginViewModel.pluginUpdateCount
                )
            }
        }
    }

    @Composable
    private fun createNavTransitions(
        bottomBarRoutes: Set<String>
    ): NavHostAnimatedDestinationStyle {
        return object : NavHostAnimatedDestinationStyle() {
            override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
                if (targetState.destination.route !in bottomBarRoutes) {
                    slideInHorizontally(initialOffsetX = { it })
                } else {
                    val initialRoute = initialState.destination.route
                    val targetRoute = targetState.destination.route
                    val initialIndex = BottomBarDestination.entries.indexOfFirst { it.direction.route == initialRoute }
                    val targetIndex = BottomBarDestination.entries.indexOfFirst { it.direction.route == targetRoute }

                    val stiffness = 300f
                    val duration300 = 300

                    if (initialIndex != -1 && targetIndex != -1) {
                        if (targetIndex > initialIndex) {
                            slideInHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = stiffness), initialOffsetX = { width -> width })
                        } else {
                            slideInHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = stiffness), initialOffsetX = { width -> -width })
                        }
                    } else {
                        fadeIn(animationSpec = tween(340))
                    }
                }
            }

            override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
                if (initialState.destination.route in bottomBarRoutes && targetState.destination.route !in bottomBarRoutes) {
                    slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()
                } else {
                    if (initialState.destination.route in bottomBarRoutes && targetState.destination.route in bottomBarRoutes) {
                        val initialRoute = initialState.destination.route
                        val targetRoute = targetState.destination.route
                        val initialIndex = BottomBarDestination.entries.indexOfFirst { it.direction.route == initialRoute }
                        val targetIndex = BottomBarDestination.entries.indexOfFirst { it.direction.route == targetRoute }

                        val stiffness = 300f
                        val duration300 = 300

                        if (initialIndex != -1 && targetIndex != -1) {
                            if (targetIndex > initialIndex) {
                                slideOutHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = stiffness), targetOffsetX = { width -> -width })
                            } else {
                                slideOutHorizontally(animationSpec = spring(dampingRatio = 0.8f, stiffness = stiffness), targetOffsetX = { width -> width })
                            }
                        } else {
                            fadeOut(animationSpec = tween(340))
                        }
                    } else {
                        fadeOut(animationSpec = tween(340))
                    }
                }
            }

            override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
                if (targetState.destination.route in bottomBarRoutes) {
                    slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn()
                } else {
                    fadeIn(animationSpec = tween(340))
                }
            }

            override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
                if (initialState.destination.route !in bottomBarRoutes) {
                    scaleOut(targetScale = 0.9f) + fadeOut()
                } else {
                    fadeOut(animationSpec = tween(340))
                }
            }
        }
    }

    @Composable
    fun BottomBar(
        navController: NavHostController,
        navigator: DestinationsNavigator,
        axeronServerInfo: AxeronInfo,
        moduleUpdateCount: Int
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                BottomBarDestination.entries
                    .forEach { destination ->
                        if (!axeronServerInfo.isRunning() && destination.needAxeron) return@forEach

                        val isCurrentDestOnBackStack by navController.isRouteOnBackStackAsState(
                            destination.direction
                        )
                        val label = stringResource(id = destination.labelId)
                        NavigationBarItem(
                            selected = isCurrentDestOnBackStack,
                            onClick = {
                                if (isCurrentDestOnBackStack) {
                                    navigator.popBackStack(destination.direction, false)
                                }
                                navigator.navigate(destination.direction) {
                                    popUpTo(NavGraphs.root) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (destination == BottomBarDestination.Plugin && moduleUpdateCount > 0) {
                                    BadgedBox(badge = { Badge { Text(moduleUpdateCount.toString()) } }) {
                                        if (isCurrentDestOnBackStack) {
                                            Icon(
                                                destination.iconSelected,
                                                label
                                            )
                                        } else {
                                            Icon(
                                                destination.iconNotSelected,
                                                label
                                            )
                                        }
                                    }
                                } else {
                                    if (isCurrentDestOnBackStack) Icon(
                                        imageVector = destination.iconSelected,
                                        contentDescription = label
                                    ) else Icon(
                                        imageVector = destination.iconNotSelected,
                                        contentDescription = label
                                    )
                                }
                            },
                            label = {
                                Text(label)
                            },
                            alwaysShowLabel = false
                        )
                    }
            }
        }
    }
}
