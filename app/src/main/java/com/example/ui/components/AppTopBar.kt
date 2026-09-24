package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CurrentScreen

@Composable
fun AnnouncementBar(
    promoText: String,
    onCodeClick: () -> Unit
) {
    Surface(
        color = NavyDark,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = AmberAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = promoText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                color = AmberAccent.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.clickable { onCodeClick() }
            ) {
                Text(
                    text = "TAP CODE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberAccent,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: CurrentScreen,
    cartCount: Int,
    wishlistCount: Int,
    userRole: String,
    isAdminLoggedIn: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onNavigate: (CurrentScreen) -> Unit,
    onBack: () -> Unit,
    onAdminClick: () -> Unit
) {
    var searchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopAppBar(
            title = {
                if (!searchExpanded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigate(CurrentScreen.HOME) }
                    ) {
                        // AT Shop Monogram Brand Badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AT",
                                color = AmberAccent,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AT",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = " SHOP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "PREMIUM STORE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateMuted,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                if (currentScreen != CurrentScreen.HOME) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("nav_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = {
                // Search Toggle
                IconButton(
                    onClick = {
                        searchExpanded = !searchExpanded
                        if (searchExpanded && currentScreen != CurrentScreen.CATALOG) {
                            onNavigate(CurrentScreen.CATALOG)
                        }
                    },
                    modifier = Modifier.testTag("search_icon_button")
                ) {
                    Icon(
                        imageVector = if (searchExpanded) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }

                // Wishlist Icon
                IconButton(
                    onClick = { onNavigate(CurrentScreen.WISHLIST) },
                    modifier = Modifier.testTag("wishlist_nav_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (wishlistCount > 0) {
                                Badge(
                                    containerColor = AmberAccent,
                                    contentColor = NavyDark
                                ) {
                                    Text(wishlistCount.toString(), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist"
                        )
                    }
                }

                // Cart Icon
                IconButton(
                    onClick = { onNavigate(CurrentScreen.CART) },
                    modifier = Modifier.testTag("cart_nav_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(
                                    containerColor = PrimaryBlue,
                                    contentColor = Color.White
                                ) {
                                    Text(cartCount.toString(), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = "Cart"
                        )
                    }
                }

                // Top Corner Admin Dashboard Button (Only owner can access)
                Surface(
                    color = if (isAdminLoggedIn) NavyDark else PrimaryBlue.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAdminLoggedIn) AmberAccent else PrimaryBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onAdminClick() }
                        .testTag("top_corner_admin_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Dashboard",
                            tint = if (isAdminLoggedIn) AmberAccent else PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAdminLoggedIn) "এডমিন (Ariful)" else "এডমিন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdminLoggedIn) AmberAccent else PrimaryBlue
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Expandable Search Bar
        AnimatedVisibility(visible = searchExpanded) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_field"),
                    placeholder = { Text("Search products, brands, tech, SKU...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SlateMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}
