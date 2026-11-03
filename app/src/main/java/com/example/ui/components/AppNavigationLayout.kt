package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserEntity
import com.example.ui.Screen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class NavigationItem(
  val screen: Screen,
  val icon: ImageVector,
  val badgeCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationLayout(
  currentScreen: Screen,
  currentUser: UserEntity?,
  onNavigate: (Screen) -> Unit,
  onLogout: () -> Unit,
  onSwitchRole: (String) -> Unit,
  content: @Composable (PaddingValues) -> Unit
) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val adminItems = listOf(
    NavigationItem(Screen.AdminDashboard, Icons.Default.Dashboard),
    NavigationItem(Screen.DataGuru, Icons.Default.People),
    NavigationItem(Screen.IuranWajib, Icons.Default.Payments),
    NavigationItem(Screen.SimpananGuru, Icons.Default.AccountBalanceWallet),
    NavigationItem(Screen.Pinjaman, Icons.Default.RequestQuote),
    NavigationItem(Screen.Angsuran, Icons.Default.CalendarMonth),
    NavigationItem(Screen.Denda, Icons.Default.Warning),
    NavigationItem(Screen.TokoKoperasi, Icons.Default.Storefront),
    NavigationItem(Screen.Laporan, Icons.Default.Assessment),
    NavigationItem(Screen.Pengaturan, Icons.Default.Settings)
  )

  val guruItems = listOf(
    NavigationItem(Screen.GuruDashboard, Icons.Default.Home),
    NavigationItem(Screen.GuruProfil, Icons.Default.Person),
    NavigationItem(Screen.GuruIuran, Icons.Default.Payments),
    NavigationItem(Screen.GuruSimpanan, Icons.Default.Savings),
    NavigationItem(Screen.GuruPinjaman, Icons.Default.RequestQuote),
    NavigationItem(Screen.GuruAngsuran, Icons.Default.CalendarMonth),
    NavigationItem(Screen.GuruDenda, Icons.Default.WarningAmber),
    NavigationItem(Screen.GuruRiwayatBelanja, Icons.Default.ReceiptLong)
  )

  val menuItems = if (currentUser?.role == "admin") adminItems else guruItems

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = CardSurface
      ) {
        // Drawer Header with School Branding
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(NavyDark)
            .padding(20.dp)
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(NavyLight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.School,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  "SMP NURUL ANSOR",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
                Text(
                  "Sistem Koperasi Sekolah",
                  color = NavyLight,
                  fontSize = 12.sp
                )
              }
            }
            Spacer(modifier = Modifier.height(14.dp))
            // Current User Info Card
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.12f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    currentUser?.namaLengkap ?: "Pengguna",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                  )
                  Text(
                    "Role: ${(currentUser?.role ?: "admin").uppercase()}",
                    color = NavyLight,
                    fontSize = 10.sp
                  )
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (currentUser?.role == "admin") PurpleAccent else SuccessGreen)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    if (currentUser?.role == "admin") "ADMIN" else "GURU",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Items List
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
        ) {
          menuItems.forEach { item ->
            val isSelected = currentScreen.route == item.screen.route
            NavigationDrawerItem(
              icon = {
                Icon(
                  item.icon,
                  contentDescription = null,
                  tint = if (isSelected) NavyPrimary else TextSecondary
                )
              },
              label = {
                Text(
                  item.screen.title,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 13.sp,
                  color = if (isSelected) NavyPrimary else TextPrimary
                )
              },
              selected = isSelected,
              onClick = {
                onNavigate(item.screen)
                scope.launch { drawerState.close() }
              },
              colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = NavyLight.copy(alpha = 0.35f),
                unselectedContainerColor = Color.Transparent
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .padding(vertical = 2.dp)
                .testTag("nav_item_${item.screen.route}")
            )
          }
        }

        HorizontalDivider(color = BorderLight)

        // Switch role demo button & Logout
        Column(modifier = Modifier.padding(12.dp)) {
          OutlinedButton(
            onClick = {
              if (currentUser?.role == "admin") {
                onSwitchRole("guru")
              } else {
                onSwitchRole("admin")
              }
              scope.launch { drawerState.close() }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("switch_role_button"),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              if (currentUser?.role == "admin") "Switch ke Akun Guru" else "Switch ke Akun Admin",
              fontSize = 11.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("logout_button"),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Keluar (Logout)", fontSize = 12.sp)
          }
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Column {
              Text(
                currentScreen.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                "SMP Nurul Ansor",
                style = MaterialTheme.typography.bodySmall,
                color = NavyLight
              )
            }
          },
          navigationIcon = {
            IconButton(
              onClick = { scope.launch { drawerState.open() } },
              modifier = Modifier.testTag("nav_drawer_button")
            ) {
              Icon(Icons.Default.Menu, contentDescription = "Menu Navigasi", tint = Color.White)
            }
          },
          actions = {
            // Quick role badge
            Box(
              modifier = Modifier
                .padding(end = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (currentUser?.role == "admin") PurpleAccent else SuccessGreen)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                if (currentUser?.role == "admin") "ADMIN" else "GURU",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyDark,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
          )
        )
      },
      content = content
    )
  }
}
