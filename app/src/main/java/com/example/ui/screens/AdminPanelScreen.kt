package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class AdminTab {
    DASHBOARD,
    PRODUCTS,
    ORDERS,
    INVENTORY,
    COUPONS,
    CMS_BANNER,
    MESSAGES,
    AUDIT_LOGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: ShopViewModel
) {
    var activeTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    val adminSession by viewModel.adminSession.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val messages by viewModel.contactMessages.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val bannerContent by viewModel.bannerContent.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()

    // Modals
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCouponDialog by remember { mutableStateOf(false) }
    var productToEditStock by remember { mutableStateOf<Product?>(null) }
    var orderToUpdateStatus by remember { mutableStateOf<Order?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_panel_screen")
    ) {
        // Owner Identity Banner with Active Session & Logout
        Surface(
            color = NavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Portal (/admin)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Protected",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "User: ariful • Token: ${adminSession?.sessionToken?.take(10) ?: "active"}",
                            color = SlateMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.adminLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDanger.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("লগআউট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Admin Navigation Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTab.ordinal,
            containerColor = NavyDark,
            contentColor = Color.White,
            edgePadding = 12.dp
        ) {
            AdminTab.values().forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = {
                        Text(
                            text = when (tab) {
                                AdminTab.DASHBOARD -> "Dashboard"
                                AdminTab.PRODUCTS -> "Products (${products.size})"
                                AdminTab.ORDERS -> "Orders (${orders.size})"
                                AdminTab.INVENTORY -> "Inventory"
                                AdminTab.COUPONS -> "Coupons"
                                AdminTab.CMS_BANNER -> "CMS & Banner"
                                AdminTab.MESSAGES -> "Messages (${messages.size})"
                                AdminTab.AUDIT_LOGS -> "Audit Logs"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == tab) AmberAccent else Color.White.copy(alpha = 0.8f)
                        )
                    }
                )
            }
        }

        // Body Content for each tab
        when (activeTab) {
            AdminTab.DASHBOARD -> {
                val totalRevenue = orders.sumOf { it.grandTotal }
                val lowStockCount = products.count { it.stock in 1..15 }
                val outOfStockCount = products.count { it.stock == 0 }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("AT Shop Live Metrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    // Stat Cards Grid
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "Total Sales",
                                value = "$${String.format("%.2f", totalRevenue)}",
                                icon = Icons.Default.AttachMoney,
                                color = PrimaryBlue,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Total Orders",
                                value = "${orders.size}",
                                icon = Icons.Default.ShoppingBag,
                                color = EmeraldSuccess,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "Catalog Items",
                                value = "${products.size}",
                                icon = Icons.Default.Inventory2,
                                color = NavyDark,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Low Stock Alerts",
                                value = "$lowStockCount",
                                icon = Icons.Default.Warning,
                                color = if (lowStockCount > 0) RoseDanger else EmeraldSuccess,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Recent Orders List
                    item {
                        Text("Recent Orders", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 10.dp))
                    }

                    items(orders.take(5)) { order ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${order.customerName} • ${order.paymentMethod}", fontSize = 11.sp, color = SlateMuted)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("$${String.format("%.2f", order.grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryBlue)
                                    Text(order.orderStatus, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }
            }

            AdminTab.PRODUCTS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("admin_add_product_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Product")
                        }
                    }

                    items(products, key = { it.id }) { product ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text("SKU: ${product.sku} | Cat: ${product.category}", fontSize = 11.sp, color = SlateMuted)
                                    Text(
                                        "Price: $${product.price} | Stock: ${product.stock} units",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (product.stock < 10) RoseDanger else EmeraldSuccess
                                    )
                                }

                                Row {
                                    IconButton(onClick = { productToEditStock = product }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Stock", tint = PrimaryBlue)
                                    }
                                    IconButton(onClick = { viewModel.adminDeleteProduct(product.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RoseDanger)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AdminTab.ORDERS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.orderId }) { order ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${order.customerName} (${order.customerEmail})", fontSize = 11.sp, color = SlateMuted)
                                    }
                                    Button(
                                        onClick = { orderToUpdateStatus = order },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(order.orderStatus, fontSize = 10.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Total: $${String.format("%.2f", order.grandTotal)} • Payment: ${order.paymentStatus} (${order.paymentMethod})", fontSize = 11.sp)
                                Text("Destination: ${order.shippingAddress.addressLine1}, ${order.shippingAddress.city}", fontSize = 11.sp, color = SlateMuted)
                            }
                        }
                    }
                }
            }

            AdminTab.INVENTORY -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Inventory & Warehouse Stock Monitoring", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    items(products) { prod ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text("SKU: ${prod.sku}", fontSize = 11.sp, color = SlateMuted)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${prod.stock} in stock",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = if (prod.stock <= 10) RoseDanger else EmeraldSuccess
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FilledIconButton(
                                        onClick = { productToEditStock = prod },
                                        modifier = Modifier.size(32.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = NavyDark)
                                    ) {
                                        Icon(Icons.Default.Tune, contentDescription = "Adjust", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AdminTab.COUPONS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddCouponDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Promo Voucher")
                        }
                    }

                    items(coupons) { c ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(c.code, fontWeight = FontWeight.Black, fontSize = 15.sp, color = PrimaryBlue)
                                    Surface(color = EmeraldSuccess.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Text(if (c.isActive) "Active" else "Disabled", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text(c.description, fontSize = 12.sp, color = SlateMuted)
                                Text("Discount: ${c.discountPercent}% off • Min spend: $${c.minSpend}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            AdminTab.CMS_BANNER -> {
                var heroTitle by remember { mutableStateOf(bannerContent.heroTitle) }
                var heroSubtitle by remember { mutableStateOf(bannerContent.heroSubtitle) }
                var heroCta by remember { mutableStateOf(bannerContent.heroCtaText) }
                var promoText by remember { mutableStateOf(bannerContent.promoBannerText) }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Live Storefront Customizer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    item {
                        OutlinedTextField(value = heroTitle, onValueChange = { heroTitle = it }, label = { Text("Hero Main Title") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = heroSubtitle, onValueChange = { heroSubtitle = it }, label = { Text("Hero Subtitle") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = heroCta, onValueChange = { heroCta = it }, label = { Text("CTA Button Text") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = promoText, onValueChange = { promoText = it }, label = { Text("Announcement Ribbon Text") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        Button(
                            onClick = {
                                viewModel.adminUpdateCMS(
                                    bannerContent.copy(
                                        heroTitle = heroTitle,
                                        heroSubtitle = heroSubtitle,
                                        heroCtaText = heroCta,
                                        promoBannerText = promoText
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Publish Storefront Changes")
                        }
                    }
                }
            }

            AdminTab.MESSAGES -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Text("No customer contact messages received yet.", fontSize = 13.sp, color = SlateMuted)
                        }
                    } else {
                        items(messages) { msg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(msg.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(msg.status, fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                    }
                                    Text("From: ${msg.name} (${msg.email})", fontSize = 11.sp, color = SlateMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(msg.message, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = { viewModel.adminUpdateMessageStatus(msg.id, "RESOLVED") }) {
                                            Text("Mark Resolved", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AdminTab.AUDIT_LOGS -> {
                val sdf = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text("Audit Trail & Administrative Activity Logs", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    items(activityLogs) { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${log.actor}: ${log.action}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(log.target, fontSize = 11.sp, color = SlateMuted)
                                }
                                Text(sdf.format(Date(log.timestamp)), fontSize = 10.sp, color = SlateMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Stock Adjustment
    if (productToEditStock != null) {
        val prod = productToEditStock!!
        var newStockStr by remember { mutableStateOf(prod.stock.toString()) }
        var reason by remember { mutableStateOf("Warehouse Restock") }

        AlertDialog(
            onDismissRequest = { productToEditStock = null },
            title = { Text("Adjust Stock: ${prod.name}", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current stock: ${prod.stock} units", fontSize = 12.sp, color = SlateMuted)
                    OutlinedTextField(
                        value = newStockStr,
                        onValueChange = { newStockStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("New Stock Count") }
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Adjustment Reason") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = newStockStr.toIntOrNull() ?: prod.stock
                        viewModel.adminAdjustStock(prod.id, count, reason)
                        productToEditStock = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Update Stock")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToEditStock = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Order Status Update
    if (orderToUpdateStatus != null) {
        val ord = orderToUpdateStatus!!
        val statusList = listOf("PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED")

        AlertDialog(
            onDismissRequest = { orderToUpdateStatus = null },
            title = { Text("Update Order #${ord.orderId} Status", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    statusList.forEach { st ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.adminUpdateOrderStatus(ord.orderId, st)
                                    orderToUpdateStatus = null
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(selected = ord.orderStatus == st, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(st, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { orderToUpdateStatus = null }) { Text("Close") }
            }
        )
    }

    // Modal: Add New Product
    if (showAddProductDialog) {
        var name by remember { mutableStateOf("") }
        var brand by remember { mutableStateOf("AT Core Tech") }
        var category by remember { mutableStateOf("Electronics & Tech") }
        var priceStr by remember { mutableStateOf("") }
        var stockStr by remember { mutableStateOf("25") }
        var sku by remember { mutableStateOf("AT-${(100..999).random()}") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add Product to Storefront", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(300.dp)) {
                    item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name *") }) }
                    item { OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand *") }) }
                    item { OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category *") }) }
                    item { OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Price ($) *") }) }
                    item { OutlinedTextField(value = stockStr, onValueChange = { stockStr = it }, label = { Text("Initial Stock *") }) }
                    item { OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU *") }) }
                    item { OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceStr.toDoubleOrNull() ?: 99.0
                        val stock = stockStr.toIntOrNull() ?: 20
                        if (name.isNotBlank()) {
                            val newProduct = Product(
                                id = "prod_${System.currentTimeMillis()}",
                                name = name,
                                slug = name.lowercase().replace(" ", "-"),
                                brand = brand,
                                category = category,
                                categorySlug = category.lowercase().replace(" & ", "-").replace(" ", "-"),
                                sku = sku,
                                price = price,
                                originalPrice = price * 1.2,
                                discountPercent = 15,
                                stock = stock,
                                rating = 4.8,
                                reviewCount = 1,
                                images = listOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80"),
                                thumbnailUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                                description = description.ifBlank { "Premium flagship quality engineered by $brand." },
                                isFeatured = true
                            )
                            viewModel.adminSaveProduct(newProduct)
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Coupon
    if (showAddCouponDialog) {
        var code by remember { mutableStateOf("") }
        var discountPct by remember { mutableStateOf("25") }
        var minSpend by remember { mutableStateOf("50") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCouponDialog = false },
            title = { Text("New Promo Voucher", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Coupon Code (e.g. FLASH30)") })
                    OutlinedTextField(value = discountPct, onValueChange = { discountPct = it }, label = { Text("Discount Percentage (%)") })
                    OutlinedTextField(value = minSpend, onValueChange = { minSpend = it }, label = { Text("Minimum Spend ($)") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (code.isNotBlank()) {
                            viewModel.adminCreateCoupon(
                                Coupon(
                                    code = code,
                                    discountPercent = discountPct.toIntOrNull() ?: 20,
                                    minSpend = minSpend.toDoubleOrNull() ?: 40.0,
                                    description = desc.ifBlank { "Promotional discount" }
                                )
                            )
                            showAddCouponDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Create Voucher")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCouponDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = SlateMuted, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
