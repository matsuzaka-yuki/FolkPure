package frb.axeron.manager.ui.screen

import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.appcompat.app.AppCompatDelegate

import frb.axeron.api.core.AxeronSettings
import androidx.lifecycle.compose.rememberLifecycleOwner
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.PaletteDialog
import frb.axeron.manager.ui.component.SearchAppBar
import frb.axeron.manager.ui.screen.settings.ActivationSettings
import frb.axeron.manager.ui.screen.settings.AppearanceSettings
import frb.axeron.manager.ui.screen.settings.ConnectionSettings
import frb.axeron.manager.ui.screen.settings.OtherSettings
import frb.axeron.manager.ui.screen.settings.PathSettings
import frb.axeron.manager.ui.viewmodel.ViewModelGlobal
import frb.axeron.manager.ui.theme.hexToColor
import frb.axeron.manager.ui.util.LocaleHelper
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsScreen(navigator: DestinationsNavigator, viewModelGlobal: ViewModelGlobal) {
    val activateViewModel = viewModelGlobal.activateViewModel
    val settings = viewModelGlobal.settingsViewModel
    
    var searchText by remember { mutableStateOf("") }
    var showDevDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val axeronRunning = activateViewModel.axeronInfo.isRunning()
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    
    var currentLanguageDisplay by remember { 
        mutableStateOf<String?>(null)
    }
    
    LaunchedEffect(Unit) {
        currentLanguageDisplay = AppCompatDelegate.getApplicationLocales()[0]?.displayLanguage?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        } ?: context.getString(R.string.system_default)
    }
    
    val prefs = AxeronSettings.getPreferences()
    val currentColor = hexToColor(settings.customPrimaryColorHex)

    val lifecycleOwner = rememberLifecycleOwner()

    DeveloperInfo(
        showDevDialog
    ) {
        showDevDialog = false
    }

    Scaffold(
        topBar = {
            SearchAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                searchLabel = stringResource(R.string.search_label),
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onClearClick = { searchText = "" },
                action = {
                    IconButton(
                        modifier = Modifier.padding(end = 5.dp),
                        onClick = {
                            showDevDialog = true
                        })
                    {
                        Icon(Icons.Outlined.Info, null)
                    }
                }
            )
        }
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ConnectionSettings(
                searchText = searchText,
                isTcpModeEnabled = settings.isTcpModeEnabled,
                tcpPortInt = settings.tcpPortInt,
                onTcpModeChange = { settings.setTcpMode(it) },
                onTcpPortChange = { settings.setTcpPort(it) }
            )

            AppearanceSettings(
                searchText = searchText,
                autoThemeEnabled = settings.getAppThemeId == 0,
                onAutoThemeChange = { enabled ->
                    if (enabled) {
                        settings.setAppTheme(0)
                    } else {
                        settings.setAppTheme(if (isDarkMode) 1 else 2)
                    }
                },
                darkModeEnabled = settings.getAppThemeId == 1,
                onDarkModeChange = { enabled ->
                    settings.setAppTheme(if (enabled) 1 else 2)
                },
                dynamicColorEnabled = settings.isDynamicColorEnabled,
                onDynamicColorChange = { settings.setDynamicColor(it) },
                currentLanguageDisplay = currentLanguageDisplay,
                onLanguageClick = {
                    LocaleHelper.launchSystemLanguageSettings(context)
                },
                onPaletteClick = {
                    showColorPicker = true
                }
            )

            ActivationSettings(
                searchText = searchText,
                axeronRunning = axeronRunning,
                isActivateOnBootEnabled = settings.isActivateOnBootEnabled,
                isIgniteWhenRelogEnabled = settings.isIgniteWhenRelogEnabled,
                onActivateOnBootChange = { settings.setActivateOnBoot(it) },
                onIgniteWhenRelogChange = { settings.setIgniteWhenRelog(it) },
                isShizukuActive = activateViewModel.isShizukuActive,
                onShizukuInterceptChange = { activateViewModel.setShizukuIntercept(it) }
            )

            if (axeronRunning) {
                PathSettings(
                    searchText = searchText,
                    navigator = navigator
                )
            }

            OtherSettings(
                searchText = searchText,
                axeronRunning = axeronRunning,
                navigator = navigator,
                settingsViewModel = settings
            )
        }
    }



    if (showColorPicker) {
        PaletteDialog(
            initialColor = currentColor,
            onDismiss = { showColorPicker = false },
            onConfirm = { hex ->
                settings.setCustomPrimaryColor(hex)
                showColorPicker = false
            },
            onReset = {
                settings.removeCustomPrimaryColor()
                showColorPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperInfo(
    showDialog: Boolean,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val githubUrl = "https://github.com/matsuzaka-yuki/FolkPure"
    val telegramUrl = "https://t.me/FolkPure"
    val sociabuzzUrl = "https://afdian.com/a/matsuzaka_yuki"

    if (showDialog) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF303030)),
                    contentAlignment = Alignment.Center
                    ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("http://q.qlogo.cn/headimg_dl?dst_uin=3231515355&spec=640&img_type=jpg")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Developer Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF303030))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Matsuzaka Yuki",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.developer_and_maintainer),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "\"明るい未来へと飛翔する\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledTonalButton(
                        onClick = { uriHandler.openUri(githubUrl) },
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_github),
                            contentDescription = "GitHub",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.github))
                    }

                    FilledTonalButton(
                        onClick = { uriHandler.openUri(telegramUrl) },
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_telegram),
                            contentDescription = "Telegram",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.telegram))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = { uriHandler.openUri(sociabuzzUrl) },
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Coffee,
                        contentDescription = "Support / Donate",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.support_or_donate))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
