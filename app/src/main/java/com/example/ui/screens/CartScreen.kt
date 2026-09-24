package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CartItem
import com.example.ui.theme.*
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    onNavigate: (CurrentScreen) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val discount by viewModel.cartDiscount.collectAsState()
    val shippingFee by viewModel.cartShippingFee.collectAsState()
    val tax by viewModel.cartTax.collectAsState()
    val grandTotal by viewModel.cartGrandTotal.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    val activeItems = remember(cartItems) { cartItems.filter { !it.savedForLater } }
    val savedItems = remember(cartItems) { cartItems.filter { it.savedForLater } }

    if (activeItems.isEmpty() && savedItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp)
                .testTag("empty_cart_view"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.RemoveShoppingCart,
                    contentDescription = null,
                    tint = SlateMuted,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Discover top deals and trending essentials in our catalog.",
                    fontSize = 13.sp,
                    color = SlateMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
                )
                Button(
                    onClick = { onNavigate(CurrentScreen.CATALOG) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Explore Catalog", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("cart_screen"),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Free Shipping Progress Notice
            item {
                val freeThreshold = storeSettings.freeShippingThreshold
                val amountNeeded = (freeThreshold - subtotal).coerceAtLeast(0.0)
                Surface(
                    color = if (amountNeeded == 0.0) EmeraldSuccess.copy(alpha = 0.12f) else PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (amountNeeded == 0.0) EmeraldSuccess else PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            if (amountNeeded == 0.0) {
                                Text(
                                    text = "Congratulations! You qualify for FREE Shipping!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldSuccess
                                )
                            } else {
                                Text(
                                    text = "Add $${String.format("%.2f", amountNeeded)} more to unlock FREE Shipping",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PrimaryBlue
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (subtotal / freeThreshold).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (amountNeeded == 0.0) EmeraldSuccess else PrimaryBlue,
                            )
                        }
                    }
                }
            }

            // Active Cart Items List
            items(activeItems, key = { it.id }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
                    onDecrease = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
                    onRemove = { viewModel.removeFromCart(item.id) },
                    onToggleSave = { viewModel.toggleSaveForLater(item.id) }
                )
            }

            // Promo Code Box
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Promotional Voucher",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (appliedCoupon != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EmeraldSuccess.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${appliedCoupon?.code} applied (-$${String.format("%.2f", discount)})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = EmeraldSuccess
                                    )
                                }
                                TextButton(onClick = { viewModel.removeCoupon() }) {
                                    Text("Remove", color = RoseDanger, fontSize = 11.sp)
                                }
                            }
                        } else {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it.uppercase() },
                                    placeholder = { Text("Code: ATSHOP40, WELCOME20", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (couponInput.isNotBlank()) {
                                            viewModel.applyCoupon(couponInput)
                                            couponInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Apply")
                                }
                            }
                        }
                    }
                }
            }

            // Order Price Breakdown Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow("Subtotal", "$${String.format("%.2f", subtotal)}")
                        if (discount > 0.0) {
                            SummaryRow("Voucher Discount", "-$${String.format("%.2f", discount)}", isHighlight = true)
                        }
                        SummaryRow(
                            "Shipping",
                            if (shippingFee == 0.0) "FREE" else "$${String.format("%.2f", shippingFee)}",
                            isGreen = shippingFee == 0.0
                        )
                        SummaryRow("Estimated Tax (${storeSettings.taxRatePercent}%)", "$${String.format("%.2f", tax)}")

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Grand Total", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text(
                                text = "$${String.format("%.2f", grandTotal)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onNavigate(CurrentScreen.CHECKOUT) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("proceed_to_checkout_button")
                        ) {
                            Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Saved For Later Items (if any)
            if (savedItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Saved for Later (${savedItems.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                items(savedItems, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = {},
                        onDecrease = {},
                        onRemove = { viewModel.removeFromCart(item.id) },
                        onToggleSave = { viewModel.toggleSaveForLater(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth().testTag("cart_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.productName,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 2
                )

                if (item.selectedSize != null || item.selectedColor != null) {
                    Text(
                        text = listOfNotNull(item.selectedColor, item.selectedSize).joinToString(" | "),
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        fontSize = 14.sp
                    )

                    if (!item.savedForLater) {
                        // Quantity Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "${item.quantity}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (item.savedForLater) "Move to Cart" else "Save for later",
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onToggleSave() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text(
                        text = "Remove",
                        fontSize = 11.sp,
                        color = RoseDanger,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onRemove() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isHighlight: Boolean = false, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isHighlight || isGreen) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isHighlight -> RoseDanger
                isGreen -> EmeraldSuccess
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
