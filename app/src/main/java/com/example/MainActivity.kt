package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.AnnouncementBar
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ATShopTheme {
                val viewModel: ShopViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val cartItems by viewModel.cartItems.collectAsState()
                val wishlistItems by viewModel.wishlistItems.collectAsState()
                val userRole by viewModel.currentUserRole.collectAsState()
                val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val bannerContent by viewModel.bannerContent.collectAsState()
                val uiMessage by viewModel.uiMessage.collectAsState()

                val allProducts by viewModel.allProducts.collectAsState()
                val catalogProducts by viewModel.catalogProducts.collectAsState()
                val selectedProduct by viewModel.selectedProduct.collectAsState()
                val selectedOrder by viewModel.selectedOrder.collectAsState()

                var showAdminLoginDialog by remember { mutableStateOf(false) }
                val snackbarHostState = remember { SnackbarHostState() }

                // Collect Wishlist Product IDs set
                val wishlistIds = remember(wishlistItems) {
                    wishlistItems.map { it.productId }.toSet()
                }

                val activeCartCount = remember(cartItems) {
                    cartItems.filter { !it.savedForLater }.sumOf { it.quantity }
                }

                // Handle Snackbar Messages
                LaunchedEffect(uiMessage) {
                    uiMessage?.let {
                        snackbarHostState.showSnackbar(it.message)
                        viewModel.clearUiMessage()
                    }
                }

                // Device Back Button Handling
                BackHandler(enabled = currentScreen != CurrentScreen.HOME) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        Column {
                            // Top Announcement Banner (Visible on Home & Catalog)
                            if (currentScreen == CurrentScreen.HOME || currentScreen == CurrentScreen.CATALOG) {
                                AnnouncementBar(
                                    promoText = bannerContent.promoBannerText,
                                    onCodeClick = {
                                        viewModel.applyCoupon("ATSHOP40")
                                    }
                                )
                            }

                            // Primary Navigation Top Bar
                            AppTopBar(
                                currentScreen = currentScreen,
                                cartCount = activeCartCount,
                                wishlistCount = wishlistItems.size,
                                userRole = userRole,
                                isAdminLoggedIn = isAdminLoggedIn,
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onSearchSubmit = { viewModel.navigateTo(CurrentScreen.CATALOG) },
                                onNavigate = { viewModel.navigateTo(it) },
                                onBack = { viewModel.navigateBack() },
                                onAdminClick = {
                                    if (isAdminLoggedIn) {
                                        viewModel.navigateTo(CurrentScreen.ADMIN_PANEL)
                                    } else {
                                        viewModel.navigateTo(CurrentScreen.ADMIN_LOGIN)
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentScreen != CurrentScreen.ADMIN_LOGIN) {
                            AppBottomNav(
                                currentScreen = currentScreen,
                                cartCount = activeCartCount,
                                wishlistCount = wishlistItems.size,
                                userRole = userRole,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                ) { innerPadding ->
                    // Admin Owner Login Security Dialog
                    if (showAdminLoginDialog) {
                        AdminLoginDialog(
                            onDismiss = { showAdminLoginDialog = false },
                            onLogin = { username, password ->
                                viewModel.attemptAdminLogin(username, password)
                            },
                            onSuccess = {
                                viewModel.navigateTo(CurrentScreen.ADMIN_PANEL)
                            }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            CurrentScreen.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    products = allProducts,
                                    wishlistProductIds = wishlistIds,
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onCategoryClick = { catSlug ->
                                        viewModel.filterByCategory(catSlug)
                                    }
                                )
                            }

                            CurrentScreen.CATALOG -> {
                                CatalogScreen(
                                    viewModel = viewModel,
                                    products = catalogProducts,
                                    wishlistProductIds = wishlistIds,
                                    onProductClick = { viewModel.openProductDetail(it) }
                                )
                            }

                            CurrentScreen.PRODUCT_DETAIL -> {
                                selectedProduct?.let { prod ->
                                    ProductDetailScreen(
                                        viewModel = viewModel,
                                        product = prod,
                                        isWishlisted = wishlistIds.contains(prod.id),
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                } ?: run {
                                    viewModel.navigateTo(CurrentScreen.HOME)
                                }
                            }

                            CurrentScreen.CART -> {
                                CartScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            CurrentScreen.WISHLIST -> {
                                WishlistScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            CurrentScreen.CHECKOUT -> {
                                CheckoutScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            CurrentScreen.ORDER_TRACKING -> {
                                selectedOrder?.let { ord ->
                                    OrderTrackingScreen(
                                        order = ord,
                                        onBackToHome = { viewModel.navigateTo(CurrentScreen.HOME) }
                                    )
                                } ?: run {
                                    viewModel.navigateTo(CurrentScreen.ACCOUNT)
                                }
                            }

                            CurrentScreen.ACCOUNT -> {
                                AccountScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onTrackOrder = { viewModel.openOrderTracking(it) }
                                )
                            }

                            CurrentScreen.ADMIN_LOGIN -> {
                                AdminLoginScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }

                            CurrentScreen.ADMIN_PANEL -> {
                                if (isAdminLoggedIn) {
                                    AdminPanelScreen(
                                        viewModel = viewModel
                                    )
                                } else {
                                    AdminLoginScreen(
                                        viewModel = viewModel,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            }

                            CurrentScreen.ABOUT_US -> {
                                AboutUsScreen()
                            }

                            CurrentScreen.CONTACT_US -> {
                                ContactUsScreen(viewModel = viewModel)
                            }

                            CurrentScreen.POLICIES -> {
                                PoliciesScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
