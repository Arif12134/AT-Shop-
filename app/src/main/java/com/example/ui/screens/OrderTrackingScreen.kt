package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Order
import com.example.model.OrderTimelineEvent
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderTrackingScreen(
    order: Order,
    onBackToHome: () -> Unit
) {
    val statuses = listOf("PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED")
    val currentStatusIndex = statuses.indexOf(order.orderStatus.uppercase()).coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("order_tracking_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.orderId}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Placed on ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(order.createdAt))}",
                                color = SlateMuted,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            color = when (order.orderStatus.uppercase()) {
                                "DELIVERED" -> EmeraldSuccess
                                "SHIPPED" -> PrimaryBlue
                                else -> AmberAccent
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = order.orderStatus,
                                color = if (order.orderStatus.uppercase() == "DELIVERED" || order.orderStatus.uppercase() == "SHIPPED") Color.White else NavyDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Stages Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        statuses.forEachIndexed { index, status ->
                            val isReached = index <= currentStatusIndex
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isReached) EmeraldSuccess else SlateMuted.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isReached) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = status.take(4),
                                    fontSize = 9.sp,
                                    color = if (isReached) Color.White else SlateMuted,
                                    fontWeight = if (isReached) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Tracking Timeline
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Shipment Activity & Courier Logs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    order.timeline.forEachIndexed { index, event ->
                        TimelineNode(
                            event = event,
                            isLast = index == order.timeline.size - 1
                        )
                    }
                }
            }
        }

        // Purchased Items in Order
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Items in this Order (${order.items.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.productName,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                                Text("Qty: ${item.quantity}  •  $${String.format("%.2f", item.price)} each", fontSize = 11.sp, color = SlateMuted)
                            }
                            Text(
                                "$${String.format("%.2f", item.price * item.quantity)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Paid Grand Total", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("$${String.format("%.2f", order.grandTotal)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PrimaryBlue)
                    }
                }
            }
        }

        // Shipping Address & Delivery Information
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Delivery Destination",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(order.shippingAddress.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(order.shippingAddress.phone, fontSize = 12.sp, color = SlateMuted)
                    Text("${order.shippingAddress.addressLine1}, ${order.shippingAddress.city} ${order.shippingAddress.postalCode}", fontSize = 12.sp, color = SlateMuted)
                    Text(order.shippingAddress.country, fontSize = 12.sp, color = SlateMuted)
                }
            }
        }
    }
}

@Composable
fun TimelineNode(event: OrderTimelineEvent, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (event.isCompleted) EmeraldSuccess else SlateMuted.copy(alpha = 0.3f))
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(if (event.isCompleted) EmeraldSuccess else SlateMuted.copy(alpha = 0.3f))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(event.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(event.description, fontSize = 12.sp, color = SlateMuted)
        }
    }
}
