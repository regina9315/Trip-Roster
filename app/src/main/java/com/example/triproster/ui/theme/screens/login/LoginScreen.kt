package com.example.triproster.ui.theme.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

@Composable
fun LoginScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Heading
        Text(
            text = "Welcome Back!", // Heading text
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 32.dp) // Adds space below the heading
        )

        // Name field (Optional)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Name")
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Password")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            // Update display name if provided
                            if (name.isNotBlank()) {
                                val update = userProfileChangeRequest {
                                    displayName = name
                                }
                                user?.updateProfile(update)?.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                        // Navigate to the Home screen after successful login
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            } else {
                                // Navigate to Home directly if no name change
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button for users who don't have an account
        TextButton(onClick = {
            navController.navigate("register") {
                popUpTo("login") { inclusive = true }
            }
        }) {
            Text("Don't have an account? Register")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(navController = NavHostController(LocalContext.current))
    }
}
