package com.ebookfrenzy.galleryapp02.ui.main


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.review.galleryapp02.ui.nav.BottomNavItem


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Gallery,
        BottomNavItem.Maps,
        BottomNavItem.ScanQr,
        BottomNavItem.GalleryMaps,
        BottomNavItem.Painting

    )

    BottomNavigation(
        backgroundColor = Color.White, // Fondo blanco
        contentColor = Color.Black, // Color de contenido negro
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        elevation = 0.dp // Añadir elevación
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            BottomNavigationItem(
                modifier = Modifier.size(35.dp)
                    .background(if (isSelected) Color.LightGray else Color.Transparent)
                    .padding(5.dp)
                ,
                icon = {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,

                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray,
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}