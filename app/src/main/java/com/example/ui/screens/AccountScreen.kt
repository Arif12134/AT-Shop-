package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.Order
import com.example.model.ShippingAddress
import com.example.ui.theme.*
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class AccountTab {
    ORDERS,
    ADDRESSES,
    PROFILE,
    SECURITY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: ShopViewModel,
    onNavigate: (CurrentScreen) -> Unit,
    onTrackOrder: (Order) -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()
    val addresses by viewModel.savedAddresses.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val userRole by viewModel.currentUserRole.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    var selectedTab by remember { mutableStateOf(AccountTab.ORDERS) }
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }

    if (showAdminLoginDialog) {
        com.example.ui.components.AdminLoginDialog(
            onDismiss = { showAdminLoginDialog = false },
            onLogin = { username, password ->
                viewModel.attemptAdminLogin(username, password)
            },
            onSuccess = {
                onNavigate(CurrentScreen.ADMIN_PANEL)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("account_screen")
    ) {
        // Account Profile Header
        Surface(
            color = NavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.fullName.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = profile.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Text(
                                text = "alex.mercer@example.com",
                                fontSize = 12.sp,
                                color = SlateMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = PrimaryBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Role: $userRole",
                                    color = PrimaryBlueLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Admin / Owner Button
                    if (isAdminLoggedIn) {
                        OutlinedButton(
                            onClick = { onNavigate(CurrentScreen.ADMIN_PANEL) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("এডমিন প্যানেল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onNavigate(CurrentScreen.ADMIN_LOGIN) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlueLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("মালিক লগইন", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AccountTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = when (tab) {
                                AccountTab.ORDERS -> "Orders"
                                AccountTab.ADDRESSES -> "Addresses"
                                AccountTab.PROFILE -> "Profile"
                                AccountTab.SECURITY -> "Security"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Content Area
        when (selectedTab) {
            AccountTab.ORDERS -> {
                if (orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SlateMuted, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Orders Yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Your placed orders and parcel tracking history will show up here.", fontSize = 12.sp, color = SlateMuted)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders, key = { it.orderId }) { order ->
                            AccountOrderCard(
                                order = order,
                                onTrack = { onTrackOrder(order) },
                                onReorder = {
                                    // Add first item back to cart
                                    order.items.firstOrNull()?.let { item ->
                                        viewModel.allProducts.value.find { it.id == item.productId }?.let { prod ->
                                            viewModel.addToCart(prod, 1)
                                            onNavigate(CurrentScreen.CART)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            AccountTab.ADDRESSES -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Button(
                            onClick = { showAddAddressDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Shipping Address")
                        }
                    }

                    items(addresses) { addr ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(addr.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (addr.isDefault) {
                                        Surface(color = PrimaryBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                            Text("Default", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(addr.phone, fontSize = 12.sp, color = SlateMuted)
                                Text("${addr.addressLine1}, ${addr.city} ${addr.postalCode}", fontSize = 12.sp)
                                Text(addr.country, fontSize = 12.sp, color = SlateMuted)
                            }
                        }
                    }
                }
            }

            AccountTab.PROFILE -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Edit Profile Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                OutlinedTextField(value = profile.fullName, onValueChange = {}, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = "alex.mercer@example.com", onValueChange = {}, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = profile.phone, onValueChange = {}, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = { /* Saved */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Save Changes")
                                }
                            }
                        }
                    }
                }
            }

            AccountTab.SECURITY -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Password & Authentication", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                OutlinedTextField(value = "••••••••••••", onValueChange = {}, label = { Text("Current Password") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = "", onValueChange = {}, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Confirm New Password") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = { /* updated */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Update Password")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddAddressDialog) {
        var newName by remember { mutableStateOf("") }
        var newPhone by remember { mutableStateOf("") }
        var newStreet by remember { mutableStateOf("") }
        var newCity by remember { mutableStateOf("") }
        var newZip by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") })
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Phone") })
                    OutlinedTextField(value = newStreet, onValueChange = { newStreet = it }, label = { Text("Street Address") })
                    OutlinedTextField(value = newCity, onValueChange = { newCity = it }, label = { Text("City") })
                    OutlinedTextField(value = newZip, onValueChange = { newZip = it }, label = { Text("Postal Code") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newStreet.isNotBlank()) {
                            viewModel.saveAddress(
                                ShippingAddress(
                                    fullName = newName,
                                    phone = newPhone,
                                    addressLine1 = newStreet,
                                    city = newCity,
                                    postalCode = newZip
                                )
                            )
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AccountOrderCard(
    order: Order,
    onTrack: () -> Unit,
    onReorder: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().testTag("account_order_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(order.createdAt)),
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }
                Surface(
                    color = when (order.orderStatus.uppercase()) {
                        "DELIVERED" -> EmeraldSuccess.copy(alpha = 0.15f)
                        "SHIPPED" -> PrimaryBlue.copy(alpha = 0.15f)
                        else -> AmberAccent.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        color = when (order.orderStatus.uppercase()) {
                            "DELIVERED" -> EmeraldSuccess
                            "SHIPPED" -> PrimaryBlue
                            else -> AmberAccent
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("${order.items.size} item(s) • Total: $${String.format("%.2f", order.grandTotal)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReorder,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Reorder", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onTrack,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track", fontSize = 11.sp)
                }
            }
        }
    }
}
