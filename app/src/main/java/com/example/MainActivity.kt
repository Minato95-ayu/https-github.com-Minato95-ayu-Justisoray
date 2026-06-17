package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InvestigationScreen
import com.example.ui.screens.ScenarioSimulationScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SimulationViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: SimulationViewModel = viewModel()
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
          ) {
            composable("home") {
              HomeScreen(
                viewModel = viewModel,
                onNavigateToScenario = { navController.navigate("scenario") },
                onNavigateToTimeline = { navController.navigate("timeline") },
                onNavigateToInvestigation = { navController.navigate("investigate") }
              )
            }
            composable("scenario") {
              ScenarioSimulationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
            composable("timeline") {
              TimelineScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
            composable("investigate") {
              InvestigationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
          }
        }
      }
    }
  }
}
