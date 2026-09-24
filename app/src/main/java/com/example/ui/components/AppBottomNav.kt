package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryBlue
import com.example.viewmodel.CurrentScreen

@Composable
fun AppBottomNav(
    currentScreen: CurrentScreen,
    cartCount: Int,
    wishlistCount: Int,
    userRole: String,
    onNavigate: (CurrentScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("app_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentScreen == CurrentScreen.HOME,
            onClick = { onNavigate(CurrentScreen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == CurrentScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("bottom_nav_home")
        )

        // 2. Catalog / Shop
        NavigationBarItem(
            selected = currentScreen == CurrentScreen.CATALOG,
            onClick = { onNavigate(CurrentScreen.CATALOG) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == CurrentScreen.CATALOG) Icons.Filled.GridView else Icons.Outlined.GridView,
                    contentDescription = "Catalog"
                )
            },
            label = { Text("Catalog", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("bottom_nav_catalog")
        )

        // 3. Wishlist
        NavigationBarItem(
            selected = currentScreen == CurrentScreen.WISHLIST,
            onClick = { onNavigate(CurrentScreen.WISHLIST) },
            icon = {
                BadgedBox(
                    badge = {
                        if (wishlistCount > 0) {
                            Badge(
                                containerColor = AmberAccent,
                                contentColor = MaterialTheme.colorScheme.surface
                            ) {
                                Text(wishlistCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentScreen == CurrentScreen.WISHLIST) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist"
                    )
                }
            },
            label = { Text("Wishlist", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("bottom_nav_wishlist")
        )

        // 4. Cart
        NavigationBarItem(
            selected = currentScreen == CurrentScreen.CART || currentScreen == CurrentScreen.CHECKOUT,
            onClick = { onNavigate(CurrentScreen.CART) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = PrimaryBlue,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(cartCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentScreen == CurrentScreen.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            },
            label = { Text("Cart", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("bottom_nav_cart")
        )

        // 5. Account (Strictly User Account in bottom navigation)
        val isAccountSelected = currentScreen == CurrentScreen.ACCOUNT
        NavigationBarItem(
            selected = isAccountSelected,
            onClick = { onNavigate(CurrentScreen.ACCOUNT) },
            icon = {
                Icon(
                    imageVector = if (isAccountSelected) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Account"
                )
            },
            label = {
                Text(
                    text = "Account",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("bottom_nav_account")
        )
    }
}
