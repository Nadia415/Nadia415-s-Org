package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.database.AppDatabase
import com.example.data.repository.KoperasiRepository
import com.example.ui.KoperasiViewModel
import com.example.ui.Screen
import com.example.ui.components.AppNavigationLayout
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Database & Repository
    val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = KoperasiRepository(database.koperasiDao())
    val viewModel = KoperasiViewModel(repository)

    setContent {
      MyApplicationTheme {
        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
        val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
        val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(snackbarMessage) {
          snackbarMessage?.let {
            scope.launch {
              snackbarHostState.showSnackbar(it)
              viewModel.clearSnackbar()
            }
          }
        }

        // Handle Back button
        BackHandler(enabled = currentScreen !is Screen.Login) {
          if (currentUser?.role == "admin" && currentScreen != Screen.AdminDashboard) {
            viewModel.navigateTo(Screen.AdminDashboard)
          } else if (currentUser?.role == "guru" && currentScreen != Screen.GuruDashboard) {
            viewModel.navigateTo(Screen.GuruDashboard)
          } else {
            viewModel.logout()
          }
        }

        Scaffold(
          snackbarHost = { SnackbarHost(snackbarHostState) },
          modifier = Modifier.fillMaxSize()
        ) { scaffoldPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(scaffoldPadding)
          ) {
            if (currentUser == null || currentScreen is Screen.Login) {
              LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                  // Screen is handled within viewModel.login
                }
              )
            } else {
              AppNavigationLayout(
                currentScreen = currentScreen,
                currentUser = currentUser,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                onLogout = { viewModel.logout() },
                onSwitchRole = { role -> viewModel.switchUserRole(role) }
              ) { contentPadding ->
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                ) {
                  when (currentScreen) {
                    Screen.AdminDashboard -> AdminDashboardScreen(
                      viewModel = viewModel,
                      onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                    Screen.DataGuru -> DataGuruScreen(viewModel = viewModel)
                    Screen.IuranWajib -> IuranWajibScreen(viewModel = viewModel)
                    Screen.SimpananGuru -> SimpananScreen(viewModel = viewModel)
                    Screen.Pinjaman -> PinjamanScreen(viewModel = viewModel)
                    Screen.Angsuran -> AngsuranScreen(viewModel = viewModel)
                    Screen.Denda -> DendaScreen(viewModel = viewModel)
                    Screen.TokoKoperasi -> TokoKoperasiScreen(viewModel = viewModel)
                    Screen.Laporan -> LaporanScreen(viewModel = viewModel)
                    Screen.Pengaturan -> PengaturanScreen(viewModel = viewModel)

                    // Guru Screens
                    Screen.GuruDashboard, Screen.GuruProfil -> GuruDashboardScreen(
                      viewModel = viewModel,
                      onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                    Screen.GuruIuran -> IuranWajibScreen(viewModel = viewModel)
                    Screen.GuruSimpanan -> SimpananScreen(viewModel = viewModel)
                    Screen.GuruPinjaman -> GuruPinjamanScreen(viewModel = viewModel)
                    Screen.GuruAngsuran -> AngsuranScreen(viewModel = viewModel)
                    Screen.GuruDenda -> DendaScreen(viewModel = viewModel)
                    Screen.GuruRiwayatBelanja -> TokoKoperasiScreen(viewModel = viewModel)

                    else -> AdminDashboardScreen(
                      viewModel = viewModel,
                      onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
