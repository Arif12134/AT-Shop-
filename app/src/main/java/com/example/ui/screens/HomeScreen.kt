package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.InitialData
import com.example.model.Category
import com.example.model.Product
import com.example.ui.components.Hero2DAnimationBanner
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    products: List<Product>,
    wishlistProductIds: Set<String>,
    onProductClick: (Product) -> Unit,
    onNavigate: (CurrentScreen) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val bannerContent by viewModel.bannerContent.collectAsState()
    val flashTimeRemaining by viewModel.flashDealTimeRemaining.collectAsState()
    var newsletterEmail by remember { mutableStateOf("") }
    var newsletterSubscribed by remember { mutableStateOf(false) }

    val featuredProducts = remember(products) { products.filter { it.isFeatured } }
    val flashProducts = remember(products) { products.filter { it.isFlashDeal } }
    val bestSellers = remember(products) { products.filter { it.isBestSeller } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Hero Banner with 2D Animation & Luxury Headlines
        item {
            Hero2DAnimationBanner(
                onExploreClick = { onNavigate(CurrentScreen.CATALOG) },
                onShopNowClick = { onNavigate(CurrentScreen.CATALOG) }
            )
        }

        // 2. Categories Horizontal Carousel
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shop by Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { onNavigate(CurrentScreen.CATALOG) }) {
                        Text("View All", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(InitialData.categories) { cat ->
                        CategoryChip(
                            category = cat,
                            onClick = { onCategoryClick(cat.slug) }
                        )
                    }
                }
            }
        }

        // 3. Flash Deals Section with Live Countdown Timer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("flash_deals_section"),
                colors = CardDefaults.cardColors(
                    containerColor = NavyDark
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = OrangeFlame,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "FLASH SALE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Limited Stock Available",
                                    color = SlateMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Countdown Timer Pill
                        Surface(
                            color = OrangeFlame.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeFlame),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = OrangeFlame,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = flashTimeRemaining,
                                    color = OrangeFlame,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(flashProducts) { prod ->
                            Box(modifier = Modifier.width(180.dp)) {
                                ProductCard(
                                    product = prod,
                                    isWishlisted = wishlistProductIds.contains(prod.id),
                                    onProductClick = { onProductClick(prod) },
                                    onAddToCart = { viewModel.addToCart(prod, 1) },
                                    onToggleWishlist = { viewModel.toggleWishlist(prod) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Featured Products Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Products",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Hand-picked for highest performance",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )
                    }
                    TextButton(onClick = { onNavigate(CurrentScreen.CATALOG) }) {
                        Text("See More", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2-Column Grid Rows
                val chunked = featuredProducts.chunked(2)
                chunked.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { prod ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod,
                                    isWishlisted = wishlistProductIds.contains(prod.id),
                                    onProductClick = { onProductClick(prod) },
                                    onAddToCart = { viewModel.addToCart(prod, 1) },
                                    onToggleWishlist = { viewModel.toggleWishlist(prod) }
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 5. Best Sellers Carousel
        if (bestSellers.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Best Sellers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Top rated by 10,000+ happy shoppers",
                                fontSize = 12.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bestSellers) { prod ->
                            Box(modifier = Modifier.width(180.dp)) {
                                ProductCard(
                                    product = prod,
                                    isWishlisted = wishlistProductIds.contains(prod.id),
                                    onProductClick = { onProductClick(prod) },
                                    onAddToCart = { viewModel.addToCart(prod, 1) },
                                    onToggleWishlist = { viewModel.toggleWishlist(prod) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Trust Badges & Guarantee
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TrustItem(
                        icon = Icons.Default.LocalShipping,
                        title = "Free Express Shipping",
                        desc = "On all orders over $99 nationwide"
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    TrustItem(
                        icon = Icons.Default.Security,
                        title = "100% Secure Checkout",
                        desc = "Encrypted payments & PCI DSS compliant"
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    TrustItem(
                        icon = Icons.Default.Replay,
                        title = "30-Day Easy Returns",
                        desc = "Hassle-free refunds & exchanges"
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    TrustItem(
                        icon = Icons.Default.SupportAgent,
                        title = "24/7 Dedicated Support",
                        desc = "Instant help from our specialist team"
                    )
                }
            }
        }

        // 7. Customer Testimonials
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Customer Reviews",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                TestimonialCard(
                    name = "Marcus Vance",
                    role = "Verified Buyer",
                    quote = "The AT Studio Pro headphones arrived the next day. Build quality and acoustic soundstage are unrivaled at this price tier.",
                    rating = 5
                )

                Spacer(modifier = Modifier.height(8.dp))

                TestimonialCard(
                    name = "Elena Rostova",
                    role = "Tech Enthusiast",
                    quote = "Ordering was lightning fast and tracking updates kept me informed every step from processing to my doorstep.",
                    rating = 5
                )
            }
        }

        // 8. Newsletter Subscription Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Join the AT Shop Club",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Get exclusive secret drops and 15% off your next purchase.",
                        fontSize = 12.sp,
                        color = SlateMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    if (newsletterSubscribed) {
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🎉 You're subscribed! Check your inbox soon.",
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = newsletterEmail,
                                onValueChange = { newsletterEmail = it },
                                placeholder = { Text("Enter your email", fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("newsletter_email_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newsletterEmail.contains("@")) {
                                        newsletterSubscribed = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("newsletter_submit_button")
                            ) {
                                Text("Join", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 9. Footer Links & Copyright
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDark)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("AT", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PrimaryBlueLight)
                    Text(" SHOP", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                }
                Text(
                    text = "Engineered for excellence. Premium e-commerce simplified.",
                    fontSize = 11.sp,
                    color = SlateMuted,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "About Us",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigate(CurrentScreen.ABOUT_US) }
                    )
                    Text(
                        text = "Contact",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigate(CurrentScreen.CONTACT_US) }
                    )
                    Text(
                        text = "Policies",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigate(CurrentScreen.POLICIES) }
                    )
                    Text(
                        text = "Admin Portal",
                        color = AmberAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate(CurrentScreen.ADMIN_PANEL) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "© 2026 AT Shop Inc. All rights reserved.",
                    fontSize = 11.sp,
                    color = SlateMuted
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("category_chip_${category.slug}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (category.iconName) {
                        "Headphones" -> Icons.Default.Headphones
                        "Devices" -> Icons.Default.Devices
                        "Checkroom" -> Icons.Default.Checkroom
                        "Watch" -> Icons.Default.Watch
                        else -> Icons.Default.WorkOutline
                    },
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${category.itemCount} items",
                    fontSize = 10.sp,
                    color = SlateMuted
                )
            }
        }
    }
}

@Composable
fun TrustItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = desc, fontSize = 11.sp, color = SlateMuted)
        }
    }
}

@Composable
fun TestimonialCard(name: String, role: String, quote: String, rating: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(rating) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"$quote\"",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Surface(color = EmeraldSuccess.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "✓ $role",
                        color = EmeraldSuccess,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
