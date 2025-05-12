package com.example.triproster.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.triproster.R
import com.example.triproster.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    var visible by remember { mutableStateOf(false) }

    // Launched effect to trigger the fade-in effect and navigate based on authentication status
    LaunchedEffect(Unit) {
        visible = true
        delay(2500) // You can adjust this to match your desired splash screen duration
        if (auth.currentUser != null) {
            navController.navigate(Routes.REGISTER) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo Image
                Image(
                    painter = painterResource(id = R.drawable.logo9),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                )

                // App Name Heading (Add additional heading)
                Text(
                    text = "Welcome to TripRoster",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp) // Add space below the heading
                )

                // App Description or subheading (Optional)
                Text(
                    text = "Plan your trips with ease!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp) // Add space below the description
                )

                // Spacer for spacing between heading and buttons
                Spacer(modifier = Modifier.height(24.dp))

                // Buttons for Login and Register
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Login")
                    }
                    Button(
                        onClick = {
                            navController.navigate(Routes.REGISTER) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }
}
