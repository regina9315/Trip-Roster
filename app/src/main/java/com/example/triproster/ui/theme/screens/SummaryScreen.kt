package com.example.triproster.ui.theme.screens.summary

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(navController: NavHostController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: return

    val summaries = remember { mutableStateMapOf<String, String>() }

    // Fetch summaries from Firebase
    LaunchedEffect(Unit) {
        val summaryRef = FirebaseDatabase.getInstance().getReference("summaries").child(userId)
        summaryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                summaries.clear()
                for (yearSnapshot in snapshot.children) {
                    val year = yearSnapshot.key ?: continue
                    val summary = yearSnapshot.getValue(String::class.java) ?: continue
                    summaries[year] = summary
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Light theme
    val lightColors = lightColorScheme(
        primary = Color(0xFF1565C0),
        onPrimary = Color.White,
        background = Color(0xFFF0F4F8),
        surface = Color.White,
        onSurface = Color.Black
    )

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("All Summaries") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            containerColor = darkColorScheme() .background
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (summaries.isEmpty()) {
                    Text("No summaries available.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    summaries.toSortedMap(compareByDescending { it.toInt() }).forEach { (year, summary) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = lightColors.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Year: $year", style = MaterialTheme.typography.titleMedium, color = lightColors.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(summary, style = MaterialTheme.typography.bodyMedium, color = lightColors.onSurface)

                                // Edit and Delete buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Edit button
                                    IconButton(onClick = {
                                        navController.navigate("edit_summary_screen/$year/${summaries[year]}") // Navigate to Edit screen
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }

                                    // Delete button
                                    IconButton(onClick = {
                                        // Delete summary from Firebase
                                        val summaryRef = FirebaseDatabase.getInstance().getReference("summaries").child(userId).child(year)
                                        summaryRef.removeValue()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
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
