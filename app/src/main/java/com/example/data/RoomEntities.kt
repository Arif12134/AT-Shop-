package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val brand: String,
    val category: String,
    val categorySlug: String,
    val sku: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val stock: Int,
    val rating: Double,
    val reviewCount: Int,
    val imagesJson: String,
    val thumbnailUrl: String,
    val description: String,
    val specificationsJson: String,
    val featuresJson: String,
    val sizesJson: String,
    val colorsJson: String,
    val tagsJson: String,
    val badge: String?,
    val isFeatured: Boolean,
    val isBestSeller: Boolean,
    val isNewArrival: Boolean,
    val isFlashDeal: Boolean,
    val status: String,
    val createdAt: Long
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val selectedSize: String?,
    val selectedColor: String?,
    val stockAvailable: Int,
    val savedForLater: Boolean
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val productId: String,
    val productName: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val inStock: Boolean,
    val addedAt: Long
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val itemsJson: String,
    val subtotal: Double,
    val discount: Double,
    val shippingFee: Double,
    val tax: Double,
    val grandTotal: Double,
    val shippingAddressJson: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: String,
    val createdAt: Long,
    val timelineJson: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val isVerifiedPurchase: Boolean
)

@Entity(tableName = "addresses")
data class ShippingAddressEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val discountPercent: Int,
    val fixedDiscount: Double,
    val minSpend: Double,
    val maxDiscount: Double,
    val isActive: Boolean,
    val description: String
)

@Entity(tableName = "messages")
data class ContactMessageEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    val status: String,
    val timestamp: Long
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val actor: String,
    val action: String,
    val target: String,
    val timestamp: Long
)
