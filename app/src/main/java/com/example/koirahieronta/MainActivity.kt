package com.example.koirahieronta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.koirahieronta.data.ClientRepository
import com.example.koirahieronta.data.DogRepository
import com.example.koirahieronta.data.MassageRepository
import com.example.koirahieronta.database.MassageDatabase
import com.example.koirahieronta.ui.theme.KoiraHierontaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var massageDatabase: MassageDatabase

    @Inject
    lateinit var massageRepository: MassageRepository

    @Inject
    lateinit var dogRepository: DogRepository

    @Inject
    lateinit var clientRepository: ClientRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoiraHierontaTheme(darkTheme = true) {
                NavigationBar()
            }
        }
    }
}


