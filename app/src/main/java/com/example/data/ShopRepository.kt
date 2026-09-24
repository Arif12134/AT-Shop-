package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ShopRepository(private val db: AppDatabase) {

    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val reviewDao = db.reviewDao()
    private val addressDao = db.addressDao()
    private val couponDao = db.couponDao()
    private val messageDao = db.messageDao()
    private val activityLogDao = db.activityLogDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existing = productDao.getAllProducts().first()
        if (existing.isEmpty()) {
            val entities = InitialData.initialProducts.map { it.toEntity() }
            productDao.insertProducts(entities)

            val couponEntities = InitialData.sampleCoupons.map { it.toEntity() }
            couponDao.insertCoupons(couponEntities)

            InitialData.sampleReviews.forEach {
                reviewDao.insertReview(it.toEntity())
            }

            // Seed a sample default address
            addressDao.insertAddress(
                ShippingAddressEntity(
                    id = "addr_default_1",
                    fullName = "Alex Mercer",
                    phone = "+1 (555) 234-5678",
                    addressLine1 = "742 Evergreen Terrace",
                    addressLine2 = "Apt 4B",
                    city = "Springfield",
                    postalCode = "97477",
                    country = "United States",
                    isDefault = true
                )
            )

            // Seed initial activity log
            activityLogDao.insertLog(
                ActivityLogEntity(
                    id = "log_init",
                    actor = "System",
                    action = "INITIALIZE_CATALOG",
                    target = "Database Seeded with ${InitialData.initialProducts.size} Products",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Products
    fun getPublishedProducts(): Flow<List<Product>> {
        return productDao.getAllPublishedProducts().map { list -> list.map { it.toModel() } }
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { list -> list.map { it.toModel() } }
    }

    suspend fun getProductById(id: String): Product? {
        return productDao.getProductById(id)?.toModel()
    }

    suspend fun insertOrUpdateProduct(product: Product, adminName: String = "Super Admin") {
        productDao.insertProduct(product.toEntity())
        activityLogDao.insertLog(
            ActivityLogEntity(
                id = "log_${System.currentTimeMillis()}",
                actor = adminName,
                action = "SAVE_PRODUCT",
                target = "${product.name} (SKU: ${product.sku})",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteProduct(id: String, adminName: String = "Super Admin") {
        productDao.deleteProductById(id)
        activityLogDao.insertLog(
            ActivityLogEntity(
                id = "log_${System.currentTimeMillis()}",
                actor = adminName,
                action = "DELETE_PRODUCT",
                target = "Product ID $id",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateStock(id: String, newStock: Int, reason: String = "Manual Inventory Adjustment", adminName: String = "Super Admin") {
        productDao.updateStock(id, newStock)
        activityLogDao.insertLog(
            ActivityLogEntity(
                id = "log_${System.currentTimeMillis()}",
                actor = adminName,
                action = "ADJUST_STOCK",
                target = "Product ID $id -> $newStock units (Reason: $reason)",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Cart
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { list ->
            list.map {
                CartItem(
                    id = it.id,
                    productId = it.productId,
                    productName = it.productName,
                    price = it.price,
                    quantity = it.quantity,
                    imageUrl = it.imageUrl,
                    selectedSize = it.selectedSize,
                    selectedColor = it.selectedColor,
                    stockAvailable = it.stockAvailable,
                    savedForLater = it.savedForLater
                )
            }
        }
    }

    suspend fun addToCart(product: Product, quantity: Int = 1, size: String? = null, color: String? = null) {
        val cartId = "${product.id}_${size ?: ""}_${color ?: ""}"
        val existing = cartDao.getCartItems().first().find { it.id == cartId }
        val newQuantity = (existing?.quantity ?: 0) + quantity
        cartDao.insertCartItem(
            CartItemEntity(
                id = cartId,
                productId = product.id,
                productName = product.name,
                price = product.price,
                quantity = newQuantity.coerceAtMost(product.stock),
                imageUrl = product.thumbnailUrl,
                selectedSize = size ?: product.sizes.firstOrNull(),
                selectedColor = color ?: product.colors.firstOrNull(),
                stockAvailable = product.stock,
                savedForLater = false
            )
        )
    }

    suspend fun updateCartQuantity(cartId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartId)
        } else {
            val item = cartDao.getCartItems().first().find { it.id == cartId } ?: return
            cartDao.updateCartItem(item.copy(quantity = quantity))
        }
    }

    suspend fun removeFromCart(cartId: String) {
        cartDao.deleteCartItem(cartId)
    }

    suspend fun toggleSaveForLater(cartId: String) {
        val item = cartDao.getCartItems().first().find { it.id == cartId } ?: return
        cartDao.updateCartItem(item.copy(savedForLater = !item.savedForLater))
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Wishlist
    fun getWishlistItems(): Flow<List<WishlistItem>> {
        return wishlistDao.getWishlistItems().map { list ->
            list.map {
                WishlistItem(
                    productId = it.productId,
                    productName = it.productName,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    rating = it.rating,
                    inStock = it.inStock,
                    addedAt = it.addedAt
                )
            }
        }
    }

    suspend fun toggleWishlist(product: Product): Boolean {
        val isWishlisted = wishlistDao.isWishlisted(product.id)
        if (isWishlisted) {
            wishlistDao.deleteWishlistItem(product.id)
            return false
        } else {
            wishlistDao.insertWishlistItem(
                WishlistItemEntity(
                    productId = product.id,
                    productName = product.name,
                    price = product.price,
                    imageUrl = product.thumbnailUrl,
                    rating = product.rating,
                    inStock = product.stock > 0,
                    addedAt = System.currentTimeMillis()
                )
            )
            return true
        }
    }

    suspend fun isWishlisted(productId: String): Boolean {
        return wishlistDao.isWishlisted(productId)
    }

    // Orders
    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { list -> list.map { it.toModel() } }
    }

    fun getOrdersByCustomer(customerId: String): Flow<List<Order>> {
        return orderDao.getOrdersByCustomer(customerId).map { list -> list.map { it.toModel() } }
    }

    suspend fun getOrderById(orderId: String): Order? {
        return orderDao.getOrderById(orderId)?.toModel()
    }

    suspend fun createOrder(order: Order) {
        orderDao.insertOrder(order.toEntity())
        // Decrement stock for purchased products
        order.items.forEach { item ->
            val product = productDao.getProductById(item.productId)
            if (product != null) {
                val newStock = (product.stock - item.quantity).coerceAtLeast(0)
                productDao.updateStock(product.id, newStock)
            }
        }
        activityLogDao.insertLog(
            ActivityLogEntity(
                id = "log_${System.currentTimeMillis()}",
                actor = order.customerName,
                action = "PLACE_ORDER",
                target = "Order #${order.orderId} ($${order.grandTotal})",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String, adminName: String = "Super Admin") {
        orderDao.updateOrderStatus(orderId, newStatus)
        activityLogDao.insertLog(
            ActivityLogEntity(
                id = "log_${System.currentTimeMillis()}",
                actor = adminName,
                action = "UPDATE_ORDER_STATUS",
                target = "Order #$orderId -> $newStatus",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Reviews
    fun getReviews(productId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForProduct(productId).map { list ->
            list.map {
                Review(
                    id = it.id,
                    productId = it.productId,
                    userName = it.userName,
                    rating = it.rating,
                    comment = it.comment,
                    date = it.date,
                    isVerifiedPurchase = it.isVerifiedPurchase
                )
            }
        }
    }

    suspend fun addReview(review: Review) {
        reviewDao.insertReview(review.toEntity())
    }

    // Addresses
    fun getAddresses(): Flow<List<ShippingAddress>> {
        return addressDao.getAllAddresses().map { list ->
            list.map {
                ShippingAddress(
                    id = it.id,
                    fullName = it.fullName,
                    phone = it.phone,
                    addressLine1 = it.addressLine1,
                    addressLine2 = it.addressLine2,
                    city = it.city,
                    postalCode = it.postalCode,
                    country = it.country,
                    isDefault = it.isDefault
                )
            }
        }
    }

    suspend fun saveAddress(address: ShippingAddress) {
        addressDao.insertAddress(
            ShippingAddressEntity(
                id = if (address.id.isBlank()) "addr_${System.currentTimeMillis()}" else address.id,
                fullName = address.fullName,
                phone = address.phone,
                addressLine1 = address.addressLine1,
                addressLine2 = address.addressLine2,
                city = address.city,
                postalCode = address.postalCode,
                country = address.country,
                isDefault = address.isDefault
            )
        )
    }

    suspend fun deleteAddress(id: String) {
        addressDao.deleteAddress(id)
    }

    // Coupons
    fun getCoupons(): Flow<List<Coupon>> {
        return couponDao.getAllCoupons().map { list ->
            list.map {
                Coupon(
                    code = it.code,
                    discountPercent = it.discountPercent,
                    fixedDiscount = it.fixedDiscount,
                    minSpend = it.minSpend,
                    maxDiscount = it.maxDiscount,
                    isActive = it.isActive,
                    description = it.description
                )
            }
        }
    }

    suspend fun validateCoupon(code: String, subtotal: Double): Coupon? {
        val couponEntity = couponDao.getCouponByCode(code.trim().uppercase()) ?: return null
        if (!couponEntity.isActive) return null
        if (subtotal < couponEntity.minSpend) return null
        return Coupon(
            code = couponEntity.code,
            discountPercent = couponEntity.discountPercent,
            fixedDiscount = couponEntity.fixedDiscount,
            minSpend = couponEntity.minSpend,
            maxDiscount = couponEntity.maxDiscount,
            isActive = couponEntity.isActive,
            description = couponEntity.description
        )
    }

    suspend fun insertCoupon(coupon: Coupon) {
        couponDao.insertCoupon(coupon.toEntity())
    }

    // Contact Messages
    fun getMessages(): Flow<List<ContactMessage>> {
        return messageDao.getAllMessages().map { list ->
            list.map {
                ContactMessage(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    subject = it.subject,
                    message = it.message,
                    status = it.status,
                    timestamp = it.timestamp
                )
            }
        }
    }

    suspend fun sendMessage(name: String, email: String, subject: String, message: String) {
        messageDao.insertMessage(
            ContactMessageEntity(
                id = "msg_${System.currentTimeMillis()}",
                name = name,
                email = email,
                subject = subject,
                message = message,
                status = "NEW",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateMessageStatus(id: String, status: String) {
        messageDao.updateMessageStatus(id, status)
    }

    // Activity Logs
    fun getActivityLogs(): Flow<List<ActivityLog>> {
        return activityLogDao.getRecentLogs().map { list ->
            list.map {
                ActivityLog(
                    id = it.id,
                    actor = it.actor,
                    action = it.action,
                    target = it.target,
                    timestamp = it.timestamp
                )
            }
        }
    }
}

// Entity <-> Model Mappers
fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        slug = slug,
        brand = brand,
        category = category,
        categorySlug = categorySlug,
        sku = sku,
        price = price,
        originalPrice = originalPrice,
        discountPercent = discountPercent,
        stock = stock,
        rating = rating,
        reviewCount = reviewCount,
        imagesJson = JSONArray(images).toString(),
        thumbnailUrl = thumbnailUrl,
        description = description,
        specificationsJson = JSONObject(specifications).toString(),
        featuresJson = JSONArray(features).toString(),
        sizesJson = JSONArray(sizes).toString(),
        colorsJson = JSONArray(colors).toString(),
        tagsJson = JSONArray(tags).toString(),
        badge = badge,
        isFeatured = isFeatured,
        isBestSeller = isBestSeller,
        isNewArrival = isNewArrival,
        isFlashDeal = isFlashDeal,
        status = status,
        createdAt = createdAt
    )
}

fun ProductEntity.toModel(): Product {
    val imagesList = mutableListOf<String>()
    try {
        val arr = JSONArray(imagesJson)
        for (i in 0 until arr.length()) imagesList.add(arr.getString(i))
    } catch (_: Exception) {}

    val specsMap = mutableMapOf<String, String>()
    try {
        val obj = JSONObject(specificationsJson)
        obj.keys().forEach { k -> specsMap[k] = obj.getString(k) }
    } catch (_: Exception) {}

    val featuresList = mutableListOf<String>()
    try {
        val arr = JSONArray(featuresJson)
        for (i in 0 until arr.length()) featuresList.add(arr.getString(i))
    } catch (_: Exception) {}

    val sizesList = mutableListOf<String>()
    try {
        val arr = JSONArray(sizesJson)
        for (i in 0 until arr.length()) sizesList.add(arr.getString(i))
    } catch (_: Exception) {}

    val colorsList = mutableListOf<String>()
    try {
        val arr = JSONArray(colorsJson)
        for (i in 0 until arr.length()) colorsList.add(arr.getString(i))
    } catch (_: Exception) {}

    val tagsList = mutableListOf<String>()
    try {
        val arr = JSONArray(tagsJson)
        for (i in 0 until arr.length()) tagsList.add(arr.getString(i))
    } catch (_: Exception) {}

    return Product(
        id = id,
        name = name,
        slug = slug,
        brand = brand,
        category = category,
        categorySlug = categorySlug,
        sku = sku,
        price = price,
        originalPrice = originalPrice,
        discountPercent = discountPercent,
        stock = stock,
        rating = rating,
        reviewCount = reviewCount,
        images = if (imagesList.isEmpty()) listOf(thumbnailUrl) else imagesList,
        thumbnailUrl = thumbnailUrl,
        description = description,
        specifications = specsMap,
        features = featuresList,
        sizes = sizesList,
        colors = colorsList,
        tags = tagsList,
        badge = badge,
        isFeatured = isFeatured,
        isBestSeller = isBestSeller,
        isNewArrival = isNewArrival,
        isFlashDeal = isFlashDeal,
        status = status,
        createdAt = createdAt
    )
}

fun Order.toEntity(): OrderEntity {
    val itemsArray = JSONArray()
    items.forEach { item ->
        val obj = JSONObject()
        obj.put("productId", item.productId)
        obj.put("productName", item.productName)
        obj.put("quantity", item.quantity)
        obj.put("price", item.price)
        obj.put("imageUrl", item.imageUrl)
        obj.put("size", item.size ?: "")
        obj.put("color", item.color ?: "")
        itemsArray.put(obj)
    }

    val addrObj = JSONObject()
    addrObj.put("fullName", shippingAddress.fullName)
    addrObj.put("phone", shippingAddress.phone)
    addrObj.put("addressLine1", shippingAddress.addressLine1)
    addrObj.put("addressLine2", shippingAddress.addressLine2)
    addrObj.put("city", shippingAddress.city)
    addrObj.put("postalCode", shippingAddress.postalCode)
    addrObj.put("country", shippingAddress.country)

    val timelineArray = JSONArray()
    timeline.forEach {
        val tObj = JSONObject()
        tObj.put("title", it.title)
        tObj.put("description", it.description)
        tObj.put("timestamp", it.timestamp)
        tObj.put("isCompleted", it.isCompleted)
        timelineArray.put(tObj)
    }

    return OrderEntity(
        orderId = orderId,
        customerId = customerId,
        customerName = customerName,
        customerEmail = customerEmail,
        customerPhone = customerPhone,
        itemsJson = itemsArray.toString(),
        subtotal = subtotal,
        discount = discount,
        shippingFee = shippingFee,
        tax = tax,
        grandTotal = grandTotal,
        shippingAddressJson = addrObj.toString(),
        paymentMethod = paymentMethod,
        paymentStatus = paymentStatus,
        orderStatus = orderStatus,
        createdAt = createdAt,
        timelineJson = timelineArray.toString()
    )
}

fun OrderEntity.toModel(): Order {
    val itemsList = mutableListOf<OrderItem>()
    try {
        val arr = JSONArray(itemsJson)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            itemsList.add(
                OrderItem(
                    productId = obj.getString("productId"),
                    productName = obj.getString("productName"),
                    quantity = obj.getInt("quantity"),
                    price = obj.getDouble("price"),
                    imageUrl = obj.getString("imageUrl"),
                    size = obj.optString("size").takeIf { it.isNotBlank() },
                    color = obj.optString("color").takeIf { it.isNotBlank() }
                )
            )
        }
    } catch (_: Exception) {}

    var address = ShippingAddress(fullName = customerName, phone = customerPhone, addressLine1 = "", city = "", postalCode = "")
    try {
        val obj = JSONObject(shippingAddressJson)
        address = ShippingAddress(
            fullName = obj.optString("fullName", customerName),
            phone = obj.optString("phone", customerPhone),
            addressLine1 = obj.optString("addressLine1", ""),
            addressLine2 = obj.optString("addressLine2", ""),
            city = obj.optString("city", ""),
            postalCode = obj.optString("postalCode", ""),
            country = obj.optString("country", "United States")
        )
    } catch (_: Exception) {}

    val timelineList = mutableListOf<OrderTimelineEvent>()
    try {
        val arr = JSONArray(timelineJson)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            timelineList.add(
                OrderTimelineEvent(
                    title = obj.getString("title"),
                    description = obj.getString("description"),
                    timestamp = obj.getLong("timestamp"),
                    isCompleted = obj.getBoolean("isCompleted")
                )
            )
        }
    } catch (_: Exception) {}

    return Order(
        orderId = orderId,
        customerId = customerId,
        customerName = customerName,
        customerEmail = customerEmail,
        customerPhone = customerPhone,
        items = itemsList,
        subtotal = subtotal,
        discount = discount,
        shippingFee = shippingFee,
        tax = tax,
        grandTotal = grandTotal,
        shippingAddress = address,
        paymentMethod = paymentMethod,
        paymentStatus = paymentStatus,
        orderStatus = orderStatus,
        createdAt = createdAt,
        timeline = timelineList
    )
}

fun Coupon.toEntity(): CouponEntity {
    return CouponEntity(
        code = code,
        discountPercent = discountPercent,
        fixedDiscount = fixedDiscount,
        minSpend = minSpend,
        maxDiscount = maxDiscount,
        isActive = isActive,
        description = description
    )
}

fun Review.toEntity(): ReviewEntity {
    return ReviewEntity(
        id = id,
        productId = productId,
        userName = userName,
        rating = rating,
        comment = comment,
        date = date,
        isVerifiedPurchase = isVerifiedPurchase
    )
}
