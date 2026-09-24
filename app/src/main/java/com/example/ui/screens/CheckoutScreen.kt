package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

enum class CheckoutStep {
    SHIPPING_ADDRESS,
    DELIVERY_METHOD,
    PAYMENT_METHOD,
    REVIEW_AND_PLACE
}

@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel,
    onNavigate: (CurrentScreen) -> Unit
) {
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val discount by viewModel.cartDiscount.collectAsState()
    val shippingFee by viewModel.cartShippingFee.collectAsState()
    val tax by viewModel.cartTax.collectAsState()
    val grandTotal by viewModel.cartGrandTotal.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var currentStep by remember { mutableStateOf(CheckoutStep.SHIPPING_ADDRESS) }

    // Shipping address form state
    var fullName by remember { mutableStateOf("Alex Mercer") }
    var phone by remember { mutableStateOf("+1 (555) 234-5678") }
    var addressLine1 by remember { mutableStateOf("742 Evergreen Terrace") }
    var addressLine2 by remember { mutableStateOf("Apt 4B") }
    var city by remember { mutableStateOf("Springfield") }
    var postalCode by remember { mutableStateOf("97477") }
    var country by remember { mutableStateOf("United States") }

    // Delivery method state
    var selectedDeliveryMethod by remember { mutableStateOf("Standard Shipping (3-5 Business Days)") }

    // Payment method state
    var selectedPaymentMethod by remember { mutableStateOf("Stripe (Credit / Debit Card)") }
    var cardNumber by remember { mutableStateOf("4242 •••• •••• 4242") }
    var cardExpiry by remember { mutableStateOf("12/28") }
    var cardCvc by remember { mutableStateOf("382") }

    // Placed order state for confirmation screen
    var confirmedOrder by remember { mutableStateOf<Order?>(null) }

    if (confirmedOrder != null) {
        // Order Confirmation Screen
        OrderConfirmationView(
            order = confirmedOrder!!,
            onTrackOrder = {
                viewModel.openOrderTracking(confirmedOrder!!)
            },
            onContinueShopping = {
                onNavigate(CurrentScreen.HOME)
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("checkout_screen"),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Checkout Step Progress Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CheckoutStepIndicator("1. Address", currentStep == CheckoutStep.SHIPPING_ADDRESS, currentStep > CheckoutStep.SHIPPING_ADDRESS)
                    CheckoutStepIndicator("2. Delivery", currentStep == CheckoutStep.DELIVERY_METHOD, currentStep > CheckoutStep.DELIVERY_METHOD)
                    CheckoutStepIndicator("3. Payment", currentStep == CheckoutStep.PAYMENT_METHOD, currentStep > CheckoutStep.PAYMENT_METHOD)
                    CheckoutStepIndicator("4. Review", currentStep == CheckoutStep.REVIEW_AND_PLACE, false)
                }
            }

            // Step 1: Shipping Address
            item {
                StepCard(
                    title = "1. Shipping Address",
                    isActive = currentStep == CheckoutStep.SHIPPING_ADDRESS,
                    isCompleted = currentStep > CheckoutStep.SHIPPING_ADDRESS,
                    onHeaderClick = { currentStep = CheckoutStep.SHIPPING_ADDRESS }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_name_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = addressLine1,
                            onValueChange = { addressLine1 = it },
                            label = { Text("Street Address *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_address_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City *") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = postalCode,
                                onValueChange = { postalCode = it },
                                label = { Text("Postal Code *") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (fullName.isNotBlank() && addressLine1.isNotBlank()) {
                                    currentStep = CheckoutStep.DELIVERY_METHOD
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Text("Continue to Delivery")
                        }
                    }
                }
            }

            // Step 2: Delivery Method
            item {
                StepCard(
                    title = "2. Delivery Method",
                    isActive = currentStep == CheckoutStep.DELIVERY_METHOD,
                    isCompleted = currentStep > CheckoutStep.DELIVERY_METHOD,
                    onHeaderClick = { currentStep = CheckoutStep.DELIVERY_METHOD }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DeliveryOptionRow(
                            title = "Standard Shipping (3-5 Business Days)",
                            priceDesc = if (shippingFee == 0.0) "FREE" else "$${String.format("%.2f", shippingFee)}",
                            selected = selectedDeliveryMethod.contains("Standard"),
                            onSelect = { selectedDeliveryMethod = "Standard Shipping (3-5 Business Days)" }
                        )

                        DeliveryOptionRow(
                            title = "AT Priority Express (1-2 Business Days)",
                            priceDesc = "$15.00",
                            selected = selectedDeliveryMethod.contains("Priority"),
                            onSelect = { selectedDeliveryMethod = "AT Priority Express (1-2 Business Days)" }
                        )

                        Button(
                            onClick = { currentStep = CheckoutStep.PAYMENT_METHOD },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Text("Continue to Payment")
                        }
                    }
                }
            }

            // Step 3: Payment Method
            item {
                StepCard(
                    title = "3. Payment Method",
                    isActive = currentStep == CheckoutStep.PAYMENT_METHOD,
                    isCompleted = currentStep > CheckoutStep.PAYMENT_METHOD,
                    onHeaderClick = { currentStep = CheckoutStep.PAYMENT_METHOD }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Payment Provider Badge Notice
                        Surface(
                            color = NavyDark.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Payment Gateway Ready: End-to-end 256-bit SSL encrypted. Test sandbox active.",
                                    fontSize = 11.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        PaymentOptionRow(
                            title = "Stripe (Credit / Debit Card)",
                            icon = Icons.Default.CreditCard,
                            selected = selectedPaymentMethod.contains("Stripe"),
                            onSelect = { selectedPaymentMethod = "Stripe (Credit / Debit Card)" }
                        )

                        AnimatedVisibility(visible = selectedPaymentMethod.contains("Stripe")) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { cardNumber = it },
                                    label = { Text("Card Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cardExpiry,
                                        onValueChange = { cardExpiry = it },
                                        label = { Text("MM/YY") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    OutlinedTextField(
                                        value = cardCvc,
                                        onValueChange = { cardCvc = it },
                                        label = { Text("CVC") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                }
                            }
                        }

                        PaymentOptionRow(
                            title = "PayPal Express Checkout",
                            icon = Icons.Default.AccountBalanceWallet,
                            selected = selectedPaymentMethod.contains("PayPal"),
                            onSelect = { selectedPaymentMethod = "PayPal Express Checkout" }
                        )

                        PaymentOptionRow(
                            title = "Mobile Wallet (bKash / Nagad / SSLCommerz)",
                            icon = Icons.Default.Smartphone,
                            selected = selectedPaymentMethod.contains("Wallet"),
                            onSelect = { selectedPaymentMethod = "Mobile Wallet (bKash / Nagad / SSLCommerz)" }
                        )

                        PaymentOptionRow(
                            title = "Cash on Delivery (Pay at Doorstep)",
                            icon = Icons.Default.LocalAtm,
                            selected = selectedPaymentMethod.contains("Cash"),
                            onSelect = { selectedPaymentMethod = "Cash on Delivery" }
                        )

                        Button(
                            onClick = { currentStep = CheckoutStep.REVIEW_AND_PLACE },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Text("Review Order")
                        }
                    }
                }
            }

            // Step 4: Review and Place Order
            item {
                StepCard(
                    title = "4. Order Review & Place",
                    isActive = currentStep == CheckoutStep.REVIEW_AND_PLACE,
                    isCompleted = false,
                    onHeaderClick = { currentStep = CheckoutStep.REVIEW_AND_PLACE }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Summary info
                        Text("Shipping to: $fullName, $addressLine1, $city", fontSize = 12.sp, color = SlateMuted)
                        Text("Delivery: $selectedDeliveryMethod", fontSize = 12.sp, color = SlateMuted)
                        Text("Payment: $selectedPaymentMethod", fontSize = 12.sp, color = SlateMuted)

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        // Financial totals
                        SummaryRow("Subtotal", "$${String.format("%.2f", subtotal)}")
                        if (discount > 0.0) SummaryRow("Discount", "-$${String.format("%.2f", discount)}", isHighlight = true)
                        SummaryRow("Shipping", if (shippingFee == 0.0) "FREE" else "$${String.format("%.2f", shippingFee)}")
                        SummaryRow("Tax", "$${String.format("%.2f", tax)}")

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total to Pay", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "$${String.format("%.2f", grandTotal)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                val address = ShippingAddress(
                                    fullName = fullName,
                                    phone = phone,
                                    addressLine1 = addressLine1,
                                    addressLine2 = addressLine2,
                                    city = city,
                                    postalCode = postalCode,
                                    country = country
                                )
                                viewModel.placeOrder(
                                    shippingAddress = address,
                                    deliveryMethod = selectedDeliveryMethod,
                                    paymentMethodName = selectedPaymentMethod,
                                    onSuccess = { order ->
                                        confirmedOrder = order
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("place_order_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Place Order ($${String.format("%.2f", grandTotal)})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutStepIndicator(title: String, isCurrent: Boolean, isPast: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isPast -> EmeraldSuccess
                        isCurrent -> PrimaryBlue
                        else -> SlateMuted.copy(alpha = 0.3f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isPast) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            } else {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) PrimaryBlue else SlateMuted
        )
    }
}

@Composable
fun StepCard(
    title: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) PrimaryBlue else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHeaderClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isActive) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                )
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                }
            }

            AnimatedVisibility(visible = isActive) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun DeliveryOptionRow(title: String, priceDesc: String, selected: Boolean, onSelect: () -> Unit) {
    Surface(
        color = if (selected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) PrimaryBlue else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected, onClick = onSelect)
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(priceDesc, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (priceDesc == "FREE") EmeraldSuccess else PrimaryBlue)
        }
    }
}

@Composable
fun PaymentOptionRow(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onSelect: () -> Unit) {
    Surface(
        color = if (selected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) PrimaryBlue else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onSelect)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun OrderConfirmationView(
    order: Order,
    onTrackOrder: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("order_confirmation_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(EmeraldSuccess.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldSuccess,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Order Placed Successfully!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Thank you, ${order.customerName}. Your order is confirmed and is now being packaged.",
            fontSize = 13.sp,
            color = SlateMuted,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow("Order ID", "#${order.orderId}")
                SummaryRow("Total Paid", "$${String.format("%.2f", order.grandTotal)}")
                SummaryRow("Payment Method", order.paymentMethod)
                SummaryRow("Estimated Delivery", "3-5 Business Days")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onTrackOrder,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("track_order_button")
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Your Order", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Continue Shopping")
        }
    }
}
