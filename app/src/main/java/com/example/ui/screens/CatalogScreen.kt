package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InitialData
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.viewmodel.FilterState
import com.example.viewmodel.ShopViewModel
import com.example.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: ShopViewModel,
    products: List<Product>,
    wishlistProductIds: Set<String>,
    onProductClick: (Product) -> Unit
) {
    val filters by viewModel.filters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("catalog_screen")
    ) {
        // Top Filter & Sort Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter trigger button
                    OutlinedButton(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("filter_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if (filters.categorySlug != null || filters.brandName != null || filters.onlyInStock || filters.onlyFlashDeals) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(color = PrimaryBlue, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    "•",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    // Sort dropdown button
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("sort_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(filters.sortOption.label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.updateFilters(filters.copy(sortOption = option))
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (filters.sortOption == option) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PrimaryBlue)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Category Chips Carousel
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filters.categorySlug == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("All Categories") }
                        )
                    }
                    items(InitialData.categories) { cat ->
                        FilterChip(
                            selected = filters.categorySlug == cat.slug,
                            onClick = {
                                if (filters.categorySlug == cat.slug) {
                                    viewModel.filterByCategory(null)
                                } else {
                                    viewModel.filterByCategory(cat.slug)
                                }
                            },
                            label = { Text(cat.name) }
                        )
                    }
                }
            }
        }

        // Active Search Header
        if (searchQuery.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Results for \"$searchQuery\" (${products.size} items)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateMuted
                )
                TextButton(onClick = { viewModel.setSearchQuery("") }) {
                    Text("Clear", color = PrimaryBlue, fontSize = 12.sp)
                }
            }
        }

        // Product Catalog Grid
        if (products.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = SlateMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No matching products found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Try adjusting your search keywords, price filters, or category selections.",
                        fontSize = 12.sp,
                        color = SlateMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val chunked = products.chunked(2)
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
    }

    // Comprehensive Filter Modal Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Products",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { viewModel.resetFilters() }) {
                        Text("Reset", color = PrimaryBlue)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Range
                Text(
                    text = "Price Range ($0 - $1,500)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$${filters.minPrice.toInt()}", fontSize = 12.sp, color = SlateMuted)
                    Text("$${filters.maxPrice.toInt()}", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = filters.maxPrice.toFloat(),
                    onValueChange = { viewModel.updateFilters(filters.copy(maxPrice = it.toDouble())) },
                    valueRange = 50f..1500f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlue,
                        activeTrackColor = PrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Availability Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Only In-Stock Items", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = filters.onlyInStock,
                        onCheckedChange = { viewModel.updateFilters(filters.copy(onlyInStock = it)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Flash Deals Only", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = filters.onlyFlashDeals,
                        onCheckedChange = { viewModel.updateFilters(filters.copy(onlyFlashDeals = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
