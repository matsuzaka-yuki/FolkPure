package frb.axeron.manager.ui.screen

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.ActivateScreenDestination
import com.ramcosta.composedestinations.generated.destinations.QuickShellScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.ModeLabelText
import frb.axeron.manager.ui.component.TonalCard
import frb.axeron.manager.ui.component.PowerDialog
import frb.axeron.manager.ui.component.rememberLoadingDialog
import frb.axeron.manager.ui.navigation.BottomBarDestination
import frb.axeron.manager.ui.util.SystemUtils
import frb.axeron.manager.ui.util.checkNewVersion
import frb.axeron.manager.ui.util.openUpdateUrl
import frb.axeron.manager.ui.viewmodel.ActivateViewModel
import frb.axeron.manager.ui.viewmodel.PluginViewModel
import frb.axeron.manager.ui.viewmodel.PrivilegeViewModel
import frb.axeron.api.Axeron
import frb.axeron.api.AxeronPluginService
import frb.axeron.api.AxeronCommandSession
import frb.axeron.api.core.Starter
import frb.axeron.api.core.AxeronSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCircleScreen(
    navigator: DestinationsNavigator,
    activateViewModel: ActivateViewModel,
    pluginViewModel: PluginViewModel,
    privilegeViewModel: PrivilegeViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = AxeronSettings.getPreferences()
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (prefs.getBoolean("auto_update_check", true)) {
            withContext(Dispatchers.IO) {
                delay(2000)
                val hasUpdate = checkNewVersion()
                if (hasUpdate) {
                    showUpdateDialog = true
                }
            }
        }
    }

    if (showUpdateDialog) {
        frb.axeron.manager.ui.component.UpdateDialog(
            onDismiss = { showUpdateDialog = false },
            onUpdate = {
                showUpdateDialog = false
                openUpdateUrl(context)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                modifier = Modifier.padding(start = 10.dp),
                                text = "v${frb.axeron.manager.BuildConfig.VERSION_NAME} (${frb.axeron.manager.BuildConfig.VERSION_CODE})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                },
                actions = {
                    val loadingDialog = rememberLoadingDialog()
                    val scope = rememberCoroutineScope()
                    val isRunning = activateViewModel.activateStatus is ActivateViewModel.ActivateStatus.Running

                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        PowerDialog(
                            onDismiss = { showDialog = false },
                            onReignite = {
                                scope.launch {
                                    val success = loadingDialog.withLoading {
                                        AxeronPluginService.igniteSuspendService()
                                    }

                                    if (success) {
                                        pluginViewModel.fetchModuleList()
                                    }
                                }
                            },
                            onShutdown = { Axeron.destroy() },
                            onRestart = {
                                Axeron.newProcess(
                                    AxeronCommandSession.getQuickCmd(
                                        Starter.internalCommand,
                                        true,
                                        false
                                    ),
                                    null,
                                    null
                                )
                            }
                        )
                    }

                    AnimatedVisibility(visible = isRunning) {
                        IconButton(
                            modifier = Modifier.padding(end = 2.dp),
                            onClick = { showDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "Power"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(end = 12.dp))
                }
            )
        },
        floatingActionButton = {
            val isRunning = activateViewModel.activateStatus is ActivateViewModel.ActivateStatus.Running
            AnimatedVisibility(visible = isRunning) {
                FloatingActionButton(
                    onClick = {
                        navigator.navigate(QuickShellScreenDestination)
                    }
                ) {
                    Icon(Icons.Filled.Terminal, null)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(0.dp))

            StatusCardCircle(activateViewModel, navigator)

            val isRunning = activateViewModel.activateStatus is ActivateViewModel.ActivateStatus.Running
            if (isRunning) {
                LaunchedEffect(Unit) {
                    pluginViewModel.fetchModuleList()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PluginCountCard(
                        Modifier.weight(1f),
                        pluginViewModel,
                        navigator
                    )
                    PrivilegeCountCard(
                        Modifier.weight(1f),
                        privilegeViewModel,
                        navigator
                    )
                }
            }

            InfoCardCircle(activateViewModel)

            LearnMoreCardCircle()

            Spacer(Modifier)
        }
    }
}

@Composable
fun StatusCardCircle(
    activateViewModel: ActivateViewModel,
    navigator: DestinationsNavigator
) {
    val axeronInfo = activateViewModel.axeronInfo
    val status = activateViewModel.activateStatus
    val isRunning = status is ActivateViewModel.ActivateStatus.Running

    TonalCard(
        containerColor = when (status) {
            is ActivateViewModel.ActivateStatus.Running -> MaterialTheme.colorScheme.secondaryContainer
            is ActivateViewModel.ActivateStatus.Updating -> MaterialTheme.colorScheme.primaryContainer
            is ActivateViewModel.ActivateStatus.NeedExtraStep -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.errorContainer
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    when (status) {
                        is ActivateViewModel.ActivateStatus.Running -> {
                        }
                        else -> {
                            navigator.navigate(ActivateScreenDestination)
                        }
                    }
                }
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRunning) {
                Icon(Icons.Outlined.CheckCircle, stringResource(R.string.home_running))
                Column(Modifier.padding(start = 20.dp)) {
                    val modeLabel = axeronInfo.serverInfo.getMode().label

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.home_running),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.width(8.dp))
                        ModeLabelText(label = modeLabel)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Version: ${axeronInfo.getVersionCode()} | Pid: ${axeronInfo.serverInfo.pid}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                when (status) {
                    is ActivateViewModel.ActivateStatus.Updating -> {
                        Icon(Icons.Outlined.SystemUpdate, stringResource(R.string.updating))
                        Column(Modifier.padding(start = 20.dp)) {
                            Text(
                                text = stringResource(R.string.updating),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.home_not_running_msg),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is ActivateViewModel.ActivateStatus.NeedExtraStep -> {
                        Icon(Icons.Outlined.Build, stringResource(R.string.home_need_fix))
                        Column(Modifier.padding(start = 20.dp)) {
                            Text(
                                text = stringResource(R.string.home_need_fix),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.home_need_fix_msg),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    else -> {
                        Icon(Icons.Outlined.Warning, stringResource(R.string.home_not_running))
                        Column(Modifier.padding(start = 20.dp)) {
                            Text(
                                text = stringResource(R.string.home_not_running),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.home_click_to_install),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PluginCountCard(
    modifier: Modifier,
    pluginViewModel: PluginViewModel,
    navigator: DestinationsNavigator
) {
    val count = pluginViewModel.plugins.size

    TonalCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(BottomBarDestination.Plugin.direction) {
                        popUpTo(NavGraphs.root) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Extension,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = if (count <= 1) {
                        stringResource(R.string.plugin)
                    } else {
                        stringResource(R.string.plugin_plural)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun PrivilegeCountCard(
    modifier: Modifier,
    privilegeViewModel: PrivilegeViewModel,
    navigator: DestinationsNavigator
) {
    val count = privilegeViewModel.privilegedCount

    TonalCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigator.navigate(BottomBarDestination.Privilege.direction) {
                        popUpTo(NavGraphs.root) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = if (count <= 1) {
                        stringResource(R.string.privilege)
                    } else {
                        stringResource(R.string.privilege_plural)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun InfoCardCircle(activateViewModel: ActivateViewModel) {
    val axeronInfo = activateViewModel.axeronInfo

    TonalCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            @Composable
            fun InfoCardItem(
                label: String,
                content: String,
                icon: @Composable () -> Unit
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    icon()
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = label, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }

            @Composable
            fun InfoCardItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, content: String) = InfoCardItem(
                label = label,
                content = content,
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            InfoCardItem(
                icon = Icons.Outlined.Apps,
                label = stringResource(R.string.app_name),
                content = "v${frb.axeron.manager.BuildConfig.VERSION_NAME}"
            )

            Spacer(Modifier.height(16.dp))

            InfoCardItem(
                icon = Icons.Outlined.PhoneAndroid,
                label = stringResource(R.string.home_device_info),
                content = SystemUtils.getDeviceInfo()
            )

            Spacer(Modifier.height(16.dp))
            InfoCardItem(
                icon = Icons.Outlined.DeveloperBoard,
                label = stringResource(R.string.home_kernel),
                content = SystemUtils.getKernelVersion()
            )

            Spacer(Modifier.height(16.dp))
            InfoCardItem(
                icon = Icons.Outlined.Info,
                label = stringResource(R.string.home_system_version),
                content = SystemUtils.getSystemVersion()
            )

            Spacer(Modifier.height(16.dp))
            InfoCardItem(
                icon = Icons.Outlined.Android,
                label = stringResource(R.string.android_version),
                content = "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
            )

            Spacer(Modifier.height(16.dp))
            InfoCardItem(
                icon = Icons.Outlined.Memory,
                label = stringResource(R.string.abi_supported),
                content = SystemUtils.getSupportedABIs()
            )

            Spacer(Modifier.height(16.dp))
            InfoCardItem(
                icon = Icons.Outlined.Shield,
                label = stringResource(R.string.home_selinux_status),
                content = axeronInfo.serverInfo.selinuxContext
            )
        }
    }
}

@Composable
fun LearnMoreCardCircle() {
    val uriHandler = LocalUriHandler.current

    TonalCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    uriHandler.openUri("https://github.com/matsuzaka-yuki/FolkPure")
                }
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.learn_more),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.learn_more_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}