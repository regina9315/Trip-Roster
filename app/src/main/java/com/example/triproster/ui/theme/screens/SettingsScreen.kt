package com.example.triproster.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.triproster.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var darkThemeEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Account", style = MaterialTheme.typography.titleMedium)
            Text("Email: ${user?.email ?: "Not signed in"}")

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                Text("Log Out", color = Color.White)
            }

            Button(
                onClick = {
                    user?.email?.let { email ->
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Change Password", color = Color.White)
            }

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Delete Account", color = Color.White)
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            user?.delete()?.addOnCompleteListener {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            }
                        }) { Text("Yes, Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    },
                    title = { Text("Delete Account") },
                    text = { Text("Are you sure you want to permanently delete your account?") }
                )
            }

            Divider()

            Text("Preferences", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dark Theme")
                Switch(checked = darkThemeEnabled, onCheckedChange = { darkThemeEnabled = it })
            }

            Spacer(Modifier.weight(1f))

            Text("App Version: 1.0.0", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("Need help? Contact: support@triproster.com", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
