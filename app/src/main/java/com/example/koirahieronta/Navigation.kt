package com.example.koirahieronta

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddChart
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.koirahieronta.screens.ClientsScreen
import com.example.koirahieronta.screens.DogsScreen
import com.example.koirahieronta.screens.HomeScreen
import com.example.koirahieronta.screens.MassagesScreen
import com.example.koirahieronta.screens.SettingsScreen
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel
import com.example.koirahieronta.viewmodels.MassageViewModel
import com.example.koirahieronta.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


enum class Destination(
    val route: String, val label: String, val icon: ImageVector, val contentDescription: String
) {
    HOME(
        "home", "Uusi", Icons.Rounded.AddChart, contentDescription = "Home/add new"
    ),
    MASSAGES("massages", "Hieronnat", Icons.Rounded.WavingHand, "Massages"), DOGS(
        "dogs", "Koirat", Icons.Rounded.Pets, "Dogs"
    ),
    CLIENTS(
        "clients", "Asiakkaat", Icons.Rounded.People, "Clients"
    ),
    SETTINGS(
        "settings", "Asetukset", Icons.Rounded.Settings, "Settings"
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    contentPadding: PaddingValues,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
        modifier = Modifier
            .padding(contentPadding)
            .consumeWindowInsets(contentPadding)
    ) {
        composable(Destination.HOME.route) {
            val massageViewModel: MassageViewModel = hiltViewModel()
            val dogViewModel: DogViewModel = hiltViewModel()
            val clientViewModel: ClientViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            HomeScreen(
                massageViewModel, dogViewModel, clientViewModel, settingsViewModel
            )
        }

        composable(Destination.MASSAGES.route) {
            val massageViewModel: MassageViewModel = hiltViewModel()
            MassagesScreen(massageViewModel)
        }

        composable(Destination.DOGS.route) {
            val dogViewModel: DogViewModel = hiltViewModel()
            val massageViewModel: MassageViewModel = hiltViewModel()
            val clientViewModel: ClientViewModel = hiltViewModel()

            DogsScreen(
                dogViewModel, massageViewModel, clientViewModel
            )
        }

        composable(Destination.CLIENTS.route) {
            val clientViewModel: ClientViewModel = hiltViewModel()
            val dogViewModel: DogViewModel = hiltViewModel()

            ClientsScreen(
                clientViewModel, dogViewModel
            )
        }

        composable(Destination.SETTINGS.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(settingsViewModel)
        }
    }
}

@Composable
fun NavigationBar() {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val persisted = LocalContext.current.contentResolver.persistedUriPermissions
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val openAlertDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100.milliseconds)
        val permission = persisted.firstOrNull {
            it.uri == settingsViewModel.saveDirectory.text.toString().toUri()
        }

        if (permission == null || (!permission.isReadPermission && !permission.isWritePermission)) {
            openAlertDialog.value = true
        }
    }

    fun onDismissRequest() {
        openAlertDialog.value = false
        navController.navigate(Destination.SETTINGS.route)
        selectedDestination = 4
    }

    if (openAlertDialog.value) {
        AlertDialog(title = {
            Text(
                "\uD83D\uDEA8 Tallennuskansio \uD83D\uDEA8",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }, text = {
            Text("Valitse tallennuskansio!!! Tiedostojen tallennus ei toimi ilman sitä!!!")
        }, onDismissRequest = {
            onDismissRequest()
        }, confirmButton = {

        }, dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }) {
                Text("OK")
            }
        })
    }

    Scaffold(bottomBar = {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets,
        ) {
            Destination.entries.forEachIndexed { index, destination ->
                NavigationBarItem(selected = selectedDestination == index, onClick = {
                    navController.navigate(route = destination.route)
                    selectedDestination = index
                }, icon = {
                    Icon(
                        destination.icon, contentDescription = destination.contentDescription
                    )
                }, label = { Text(destination.label) })
            }
        }
    }) { contentPadding ->
        AppNavHost(
            navController, startDestination, contentPadding
        )
    }
}

