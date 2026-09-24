package com.example.model

data class Product(
    val id: String,
    val name: String,
    val slug: String,
    val brand: String,
    val category: String,
    val categorySlug: String,
    val sku: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int = 0,
    val stock: Int,
    val rating: Double,
    val reviewCount: Int,
    val images: List<String>,
    val thumbnailUrl: String,
    val description: String,
    val specifications: Map<String, String> = emptyMap(),
    val features: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val badge: String? = null,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false,
    val isFlashDeal: Boolean = false,
    val status: String = "PUBLISHED",
    val createdAt: Long = System.currentTimeMillis()
)

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val selectedSize: String? = null,
    val selectedColor: String? = null,
    val stockAvailable: Int = 99,
    val savedForLater: Boolean = false
)

data class WishlistItem(
    val productId: String,
    val productName: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val inStock: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val iconName: String,
    val itemCount: Int = 0
)

data class Brand(
    val id: String,
    val name: String,
    val slug: String,
    val logoUrl: String = "",
    val description: String = ""
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String,
    val size: String? = null,
    val color: String? = null
)

data class OrderTimelineEvent(
    val title: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean
)

data class ShippingAddress(
    val id: String = "",
    val fullName: String,
    val phone: String,
    val addressLine1: String,
    val addressLine2: String = "",
    val city: String,
    val postalCode: String,
    val country: String = "United States",
    val isDefault: Boolean = false
)

data class Order(
    val orderId: String,
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discount: Double,
    val shippingFee: Double,
    val tax: Double,
    val grandTotal: Double,
    val shippingAddress: ShippingAddress,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: String,
    val createdAt: Long,
    val timeline: List<OrderTimelineEvent> = emptyList()
)

data class Coupon(
    val code: String,
    val discountPercent: Int,
    val fixedDiscount: Double = 0.0,
    val minSpend: Double = 0.0,
    val maxDiscount: Double = 500.0,
    val isActive: Boolean = true,
    val description: String = ""
)

data class Review(
    val id: String,
    val productId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val isVerifiedPurchase: Boolean = true
)

data class FlashDeal(
    val id: String,
    val title: String,
    val discountPercent: Int,
    val endTimeMillis: Long,
    val productIds: List<String>,
    val isActive: Boolean = true
)

data class StoreSettings(
    val storeName: String = "AT Shop",
    val currencySymbol: String = "$",
    val taxRatePercent: Double = 5.0,
    val standardShippingFee: Double = 10.0,
    val freeShippingThreshold: Double = 99.0,
    val supportEmail: String = "support@atshop.com",
    val supportPhone: String = "+1 (800) 555-0199"
)

data class ActivityLog(
    val id: String,
    val actor: String,
    val action: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ContactMessage(
    val id: String,
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    val status: String = "NEW",
    val timestamp: Long = System.currentTimeMillis()
)

data class BannerContent(
    val heroTitle: String = "Upgrade Your World with AT Shop",
    val heroSubtitle: String = "Discover exclusive flagship technology, designer fashion, and premium essentials delivered straight to your door.",
    val heroCtaText: String = "Explore Catalog",
    val promoBannerText: String = "FLASH SALE: Get up to 40% OFF with code ATSHOP40",
    val isPromoBannerVisible: Boolean = true
)
