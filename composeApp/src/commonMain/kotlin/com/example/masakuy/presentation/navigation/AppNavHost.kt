package com.example.masakuy.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.masakuy.theme.OrangeMain
import com.example.masakuy.presentation.screens.auth.LoginScreen
import com.example.masakuy.presentation.screens.detail.DetailScreen
import com.example.masakuy.presentation.screens.favorite.FavoriteScreen
import com.example.masakuy.presentation.screens.home.HomeScreen
import com.example.masakuy.presentation.screens.recommendation.RecommendationScreen
import com.example.masakuy.presentation.screens.search.SearchScreen

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Routes.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Cari", Routes.Search.route, Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem("Favorit", Routes.Favorite.route, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomNavItem("Akun", "akun", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle),
)

val bottomNavRoutes = setOf(
    Routes.Home.route,
    Routes.Search.route,
    Routes.Favorite.route,
    "akun"
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes ||
        currentRoute?.startsWith("recommendation") == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (item.route == "akun") return@NavigationBarItem
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OrangeMain,
                                selectedTextColor = OrangeMain,
                                unselectedIconColor = Color(0xFFAAAAAA),
                                unselectedTextColor = Color(0xFFAAAAAA),
                                indicatorColor = Color(0xFFFFF0E8)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Home.route) {
                HomeScreen(
                    onRecommendationClick = { budget ->
                        navController.navigate(Routes.Recommendation.createRoute(budget))
                    },
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.Detail.createRoute(recipeId))
                    },
                    onFavoriteClick = {
                        navController.navigate(Routes.Favorite.route)
                    },
                    onSearchClick = {
                        navController.navigate(Routes.Search.route)
                    }
                )
            }
            composable(Routes.Recommendation.route) { backStackEntry ->
                val budget = backStackEntry.arguments?.getString("budget")?.toIntOrNull() ?: 0
                RecommendationScreen(
                    budget = budget,
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.Detail.createRoute(recipeId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Routes.Detail.route) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                DetailScreen(
                    recipeId = recipeId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Routes.Favorite.route) {
                FavoriteScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.Detail.createRoute(recipeId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Routes.Search.route) {
                SearchScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Routes.Detail.createRoute(recipeId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
