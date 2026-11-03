package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.KoperasiViewModel
import com.example.ui.theme.*

@Composable
fun LoginScreen(
  viewModel: KoperasiViewModel,
  onLoginSuccess: () -> Unit
) {
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
  ) {
    // Header curved background
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .background(NavyDark)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // Logo & Title
      Card(
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.size(90.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_koperasi_logo),
          contentDescription = "Logo Koperasi SMP Nurul Ansor",
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Sistem Koperasi Sekolah",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      Text(
        text = "SMP NURUL ANSOR",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = NavyLight,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Login Card
      ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 480.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Silakan Masuk",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NavyDark
          )
          Text(
            text = "Gunakan akun Admin atau Guru Anda",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Username Field
          OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            label = { Text("Username") },
            placeholder = { Text("admin / ahmad") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("username_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password Field
          OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
            placeholder = { Text("Masukkan password") },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = NavyPrimary)
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = errorMessage!!,
              color = DangerRed,
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Submit Button
          Button(
            onClick = {
              if (username.isBlank() || password.isBlank()) {
                errorMessage = "Username dan password tidak boleh kosong!"
                return@Button
              }
              isLoading = true
              viewModel.login(username, password) { success ->
                isLoading = false
                if (success) {
                  onLoginSuccess()
                } else {
                  errorMessage = "Username atau password salah!"
                }
              }
            },
            enabled = !isLoading,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (isLoading) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
              Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Masuk ke Sistem", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          HorizontalDivider(color = BorderLight)

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Pilih Akun Demo Cepat:",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick Demo Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                username = "admin"
                password = "admin123"
                viewModel.login("admin", "admin123") { if (it) onLoginSuccess() }
              },
              modifier = Modifier
                .weight(1f)
                .testTag("quick_admin_login"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDark)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Admin Koperasi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("(admin)", fontSize = 10.sp, color = TextMuted)
              }
            }

            OutlinedButton(
              onClick = {
                username = "ahmad"
                password = "guru123"
                viewModel.login("ahmad", "guru123") { if (it) onLoginSuccess() }
              },
              modifier = Modifier
                .weight(1f)
                .testTag("quick_guru_login"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Guru (Ahmad)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("(ahmad)", fontSize = 10.sp, color = TextMuted)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = {
              username = "budi"
              password = "guru123"
              viewModel.login("budi", "guru123") { if (it) onLoginSuccess() }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("quick_guru_budi_login"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Guru (Budi Santoso - Uji Coba Iuran Belum Lunas & Denda)", fontSize = 11.sp, textAlign = TextAlign.Center)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Footer
      Text(
        text = "Sistem Koperasi SMP Nurul Ansor v1.0\nTransparan • Akuntabel • Terintegrasi",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted
      )
    }
  }
}
