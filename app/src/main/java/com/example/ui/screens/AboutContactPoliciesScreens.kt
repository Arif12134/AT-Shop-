package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ShopViewModel

@Composable
fun AboutUsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("about_us_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("ABOUT AT SHOP", color = AmberAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Engineering Premium Modern Commerce",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Founded with a singular mission: to provide discerning customers with access to meticulously curated, precision-engineered lifestyle gear, flagship audio, and timeless urban apparel.",
                        fontSize = 13.sp,
                        color = SlateMuted,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Our Core Pillars", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    PillarItem(
                        title = "1. Authentic Craftsmanship",
                        desc = "Every item is rigorously tested in our certified acoustic and durability labs before entering the catalog."
                    )
                    PillarItem(
                        title = "2. Transparent Security",
                        desc = "No hidden fees, no opaque terms. Bank-grade 256-bit encryption protecting every transaction."
                    )
                    PillarItem(
                        title = "3. Carbon-Neutral Shipping",
                        desc = "100% recyclable packaging and eco-routed logistics network across domestic and global corridors."
                    )
                }
            }
        }
    }
}

@Composable
fun ContactUsScreen(viewModel: ShopViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("contact_us_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Contact AT Shop Support", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
            Text("Have questions regarding an order, partnership, or custom specification? We're here 24/7.", fontSize = 12.sp, color = SlateMuted)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("contact_name_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address *") },
                        modifier = Modifier.fillMaxWidth().testTag("contact_email_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject *") },
                        modifier = Modifier.fillMaxWidth().testTag("contact_subject_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message *") },
                        modifier = Modifier.fillMaxWidth().height(120.dp).testTag("contact_message_input"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
                                viewModel.sendContactMessage(name, email, subject, message)
                                name = ""
                                email = ""
                                subject = ""
                                message = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("contact_send_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Message", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Direct Channels", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("• Email: support@atshop.com", fontSize = 12.sp, color = SlateMuted)
                    Text("• Phone: +1 (800) 555-ATSHOP (Toll Free)", fontSize = 12.sp, color = SlateMuted)
                    Text("• Head Office: 100 Innovation Boulevard, Tech District, CA", fontSize = 12.sp, color = SlateMuted)
                }
            }
        }
    }
}

@Composable
fun PoliciesScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("policies_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Store Policies & Guarantees", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
        }

        item {
            PolicyAccordion(
                title = "Shipping & Logistics Policy",
                content = "Orders placed before 2:00 PM EST are packaged and dispatched same day. Standard delivery takes 3 to 5 business days, with real-time GPS tracking provided once couriers pick up the parcel. Orders above $99 automatically qualify for free expedited transit."
            )
        }

        item {
            PolicyAccordion(
                title = "30-Day Money Back & Refund Guarantee",
                content = "If you are not 100% satisfied with your AT Shop gear, return it within 30 days of delivery in original condition for an instant refund or free exchange. Prepaid return labels are provided through your account portal."
            )
        }

        item {
            PolicyAccordion(
                title = "Privacy & Data Protection Policy",
                content = "AT Shop never sells or leases your personal information to third-party ad brokers. All payment details are processed under Level-1 PCI DSS compliance using tokenized end-to-end encryption."
            )
        }

        item {
            PolicyAccordion(
                title = "Terms of Service",
                content = "By accessing AT Shop, customers agree to respectful use of our shopping and review platforms. Product warranties cover manufacturer defects for 24 months from the purchase date."
            )
        }
    }
}

@Composable
fun PillarItem(title: String, desc: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryBlue)
        Text(desc, fontSize = 12.sp, color = SlateMuted)
    }
}

@Composable
fun PolicyAccordion(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(content, fontSize = 12.sp, color = SlateMuted, lineHeight = 18.sp)
        }
    }
}
