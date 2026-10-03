package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val profiles by viewModel.profiles.collectAsStateWithLifecycle()
                val virtualApps by viewModel.virtualApps.collectAsStateWithLifecycle()
                val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

                val items = listOf(
                    Screen.Dashboard,
                    Screen.Profiles,
                    Screen.VirtualApps,
                    Screen.Runner,
                    Screen.Architecture
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                profiles = profiles,
                                virtualApps = virtualApps,
                                auditLogs = auditLogs,
                                onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) },
                                onNavigateToApps = { navController.navigate(Screen.VirtualApps.route) },
                                onNavigateToRunner = { navController.navigate(Screen.Runner.route) }
                            )
                        }
                        composable(Screen.Profiles.route) {
                            ProfilesScreen(
                                profiles = profiles,
                                onAddProfile = { name, pkg, account -> viewModel.addProfile(name, pkg, account) },
                                onDeleteProfile = { profile -> viewModel.deleteProfile(profile) }
                            )
                        }
                        composable(Screen.VirtualApps.route) {
                            VirtualAppsScreen(
                                virtualApps = virtualApps,
                                onAddVirtualApp = { name, path, sdk, clazz, ver -> viewModel.addVirtualApp(name, path, sdk, clazz, ver) },
                                onDeleteVirtualApp = { app -> viewModel.deleteVirtualApp(app) }
                            )
                        }
                        composable(Screen.Runner.route) {
                            PoCRunnerScreen(
                                profiles = profiles,
                                virtualApps = virtualApps,
                                onRunPoC = { profileId, profileName, appName, action, callback ->
                                    viewModel.runSandboxPoC(profileId, profileName, appName, action, callback)
                                }
                            )
                        }
                        composable(Screen.Architecture.route) {
                            ArchitectureDocScreen()
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "ホーム", Icons.Default.Dashboard)
    object Profiles : Screen("profiles", "プロファイル", Icons.Default.Person)
    object VirtualApps : Screen("virtual_apps", "仮想アプリ", Icons.Default.Apps)
    object Runner : Screen("runner", "PoC検証", Icons.Default.PlayArrow)
    object Architecture : Screen("architecture", "設計書", Icons.Default.Architecture)
}
