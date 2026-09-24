package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.*
import com.example.service.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class CurrentScreen(val route: String) {
    HOME("/"),
    CATALOG("/shop"),
    PRODUCT_DETAIL("/product"),
    CART("/cart"),
    WISHLIST("/wishlist"),
    CHECKOUT("/checkout"),
    ORDER_TRACKING("/account/order"),
    ACCOUNT("/account"),
    ADMIN_LOGIN("/admin/login"),
    ADMIN_PANEL("/admin"),
    ABOUT_US("/about"),
    CONTACT_US("/contact"),
    POLICIES("/policies");

    companion object {
        fun fromRoute(route: String): CurrentScreen {
            val normalized = route.trim().lowercase()
            return entries.firstOrNull { it.route.lowercase() == normalized } ?: HOME
        }
    }
}

enum class SortOption(val label: String) {
    RELEVANCE("Relevance"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Highest Rated"),
    NEWEST("Newest Arrivals")
}

data class FilterState(
    val categorySlug: String? = null,
    val brandName: String? = null,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 1500.0,
    val minRating: Double = 0.0,
    val onlyInStock: Boolean = false,
    val onlyFlashDeals: Boolean = false,
    val sortOption: SortOption = SortOption.RELEVANCE
)

data class UiMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isError: Boolean = false
)

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = ShopRepository(db)

    // Current navigation screen
    private val _currentScreen = MutableStateFlow(CurrentScreen.HOME)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    // Screen navigation stack for back button support
    private val screenStack = ArrayDeque<CurrentScreen>()

    // Selected product for detail page
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Selected order for tracking
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    // Notifications / Toasts
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    // Products Flow
    val allProducts: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedProducts: StateFlow<List<Product>> = repository.getPublishedProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart and Wishlist
    val cartItems: StateFlow<List<CartItem>> = repository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistItem>> = repository.getWishlistItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedAddresses: StateFlow<List<ShippingAddress>> = repository.getAddresses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<Coupon>> = repository.getCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLog>> = repository.getActivityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contactMessages: StateFlow<List<ContactMessage>> = repository.getMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Catalog Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(FilterState())
    val filters: StateFlow<FilterState> = _filters.asStateFlow()

    // Filtered & Sorted Catalog Products
    val catalogProducts: StateFlow<List<Product>> = combine(
        publishedProducts,
        searchQuery,
        filters
    ) { products, query, filter ->
        var list = products

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.sku.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }

        if (filter.categorySlug != null) {
            list = list.filter { it.categorySlug == filter.categorySlug }
        }

        if (filter.brandName != null) {
            list = list.filter { it.brand.equals(filter.brandName, ignoreCase = true) }
        }

        list = list.filter { it.price in filter.minPrice..filter.maxPrice }

        if (filter.minRating > 0) {
            list = list.filter { it.rating >= filter.minRating }
        }

        if (filter.onlyInStock) {
            list = list.filter { it.stock > 0 }
        }

        if (filter.onlyFlashDeals) {
            list = list.filter { it.isFlashDeal }
        }

        when (filter.sortOption) {
            SortOption.RELEVANCE -> list
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flash sale countdown timer state
    private val _flashDealTimeRemaining = MutableStateFlow("14h 42m 19s")
    val flashDealTimeRemaining: StateFlow<String> = _flashDealTimeRemaining.asStateFlow()
    private var countdownJob: Job? = null

    // Store Settings
    private val _storeSettings = MutableStateFlow(StoreSettings())
    val storeSettings: StateFlow<StoreSettings> = _storeSettings.asStateFlow()

    // Banner & CMS Content
    private val _bannerContent = MutableStateFlow(BannerContent())
    val bannerContent: StateFlow<BannerContent> = _bannerContent.asStateFlow()

    // Applied Coupon
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    // Admin Session & Authentication State
    private val _adminSession = MutableStateFlow<AdminSession?>(null)
    val adminSession: StateFlow<AdminSession?> = _adminSession.asStateFlow()

    private val _failedLoginAttempts = MutableStateFlow(0)
    val failedLoginAttempts: StateFlow<Int> = _failedLoginAttempts.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _currentUserRole = MutableStateFlow("Customer")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    fun attemptAdminLoginDetailed(username: String, pass: String): AdminLoginResult {
        val cleanUser = username.trim()
        val cleanPass = pass.trim()

        // 1. Check Lockout state
        if (_failedLoginAttempts.value >= 5) {
            val errMsg = "Security lockout: 5 consecutive failed attempts. Admin portal is temporarily locked."
            showMessage(errMsg, isError = true)
            return AdminLoginResult.Error(
                message = errMsg,
                field = LoginField.GENERAL,
                remainingAttempts = 0,
                isLockedOut = true
            )
        }

        // 2. Validate empty username
        if (cleanUser.isEmpty()) {
            val errMsg = "Username is required. Please enter 'ariful'."
            return AdminLoginResult.Error(
                message = errMsg,
                field = LoginField.USERNAME,
                remainingAttempts = 5 - _failedLoginAttempts.value
            )
        }

        // 3. Validate empty password
        if (cleanPass.isEmpty()) {
            val errMsg = "Password is required. Please enter '123456'."
            return AdminLoginResult.Error(
                message = errMsg,
                field = LoginField.PASSWORD,
                remainingAttempts = 5 - _failedLoginAttempts.value
            )
        }

        // 4. Validate credentials against username 'ariful' and password '123456'
        val isUserValid = cleanUser.equals("ariful", ignoreCase = true)
        val isPassValid = cleanPass == "123456"

        return if (isUserValid && isPassValid) {
            _failedLoginAttempts.value = 0
            val session = AdminSession(username = cleanUser)
            _adminSession.value = session
            _isAdminLoggedIn.value = true
            _currentUserRole.value = "Website Owner (Ariful)"
            showMessage("স্বাগতম আরিফুল! /admin রুটে সফলভাবে প্রবেশ করেছেন।")
            navigateTo(CurrentScreen.ADMIN_PANEL)
            AdminLoginResult.Success(session)
        } else {
            _failedLoginAttempts.value += 1
            val remaining = (5 - _failedLoginAttempts.value).coerceAtLeast(0)
            val isLocked = remaining == 0
            val errMsg = if (isLocked) {
                "সর্বোচ্চ ৫ বার ভুল প্রচেষ্টা করা হয়েছে! এডমিন পোর্টাল সাময়িকভাবে লক করা হয়েছে।"
            } else {
                "ভুল ইউজারনেম বা পাসওয়ার্ড! সঠিক তথ্য দিন (অবশিষ্ট প্রচেষ্টা: $remaining)"
            }
            val field = if (!isUserValid && isPassValid) LoginField.USERNAME
                        else if (isUserValid && !isPassValid) LoginField.PASSWORD
                        else LoginField.GENERAL

            showMessage(errMsg, isError = true)
            AdminLoginResult.Error(
                message = errMsg,
                field = field,
                remainingAttempts = remaining,
                isLockedOut = isLocked
            )
        }
    }

    fun attemptAdminLogin(username: String, pass: String): Boolean {
        return attemptAdminLoginDetailed(username, pass) is AdminLoginResult.Success
    }

    fun isSessionValid(): Boolean {
        val session = _adminSession.value ?: return false
        val valid = session.isValid
        if (!valid && _isAdminLoggedIn.value) {
            adminLogout()
        }
        return valid
    }

    fun adminLogout() {
        _adminSession.value = null
        _isAdminLoggedIn.value = false
        _currentUserRole.value = "Customer"
        showMessage("এডমিন সেশন সমাপ্ত হয়েছে। /admin/login এ রিডাইরেক্ট করা হলো।")
        navigateTo(CurrentScreen.ADMIN_LOGIN)
    }

    private val _userProfile = MutableStateFlow(
        ShippingAddress(
            id = "user_primary",
            fullName = "Alex Mercer",
            phone = "+1 (555) 234-5678",
            addressLine1 = "742 Evergreen Terrace",
            addressLine2 = "Apt 4B",
            city = "Springfield",
            postalCode = "97477",
            country = "United States"
        )
    )
    val userProfile: StateFlow<ShippingAddress> = _userProfile.asStateFlow()

    // Cart calculations
    val cartSubtotal: StateFlow<Double> = cartItems.map { items ->
        items.filter { !it.savedForLater }.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartDiscount: StateFlow<Double> = combine(cartSubtotal, appliedCoupon) { subtotal, coupon ->
        if (coupon == null || subtotal <= 0.0) 0.0
        else {
            val percentageDiscount = if (coupon.discountPercent > 0) subtotal * (coupon.discountPercent / 100.0) else 0.0
            val totalDisc = percentageDiscount + coupon.fixedDiscount
            totalDisc.coerceAtMost(coupon.maxDiscount)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartShippingFee: StateFlow<Double> = combine(cartSubtotal, storeSettings) { subtotal, settings ->
        if (subtotal == 0.0 || subtotal >= settings.freeShippingThreshold) 0.0 else settings.standardShippingFee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTax: StateFlow<Double> = combine(cartSubtotal, cartDiscount, storeSettings) { subtotal, discount, settings ->
        val taxableAmount = (subtotal - discount).coerceAtLeast(0.0)
        taxableAmount * (settings.taxRatePercent / 100.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartGrandTotal: StateFlow<Double> = combine(
        cartSubtotal,
        cartDiscount,
        cartShippingFee,
        cartTax
    ) { subtotal, discount, shipping, tax ->
        ((subtotal - discount).coerceAtLeast(0.0) + shipping + tax)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        startFlashDealCountdown()
    }

    private fun startFlashDealCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            val targetTime = InitialData.sampleFlashDeal.endTimeMillis
            while (true) {
                val now = System.currentTimeMillis()
                val diff = targetTime - now
                if (diff <= 0) {
                    _flashDealTimeRemaining.value = "00h 00m 00s"
                    break
                }
                val hours = diff / (1000 * 60 * 60)
                val mins = (diff / (1000 * 60)) % 60
                val secs = (diff / 1000) % 60
                _flashDealTimeRemaining.value = String.format("%02dh %02dm %02ds", hours, mins, secs)
                delay(1000)
            }
        }
    }

    // Navigation with Route Protection
    fun navigateTo(screen: CurrentScreen) {
        // Protect the '/admin' route
        val targetScreen = if (screen == CurrentScreen.ADMIN_PANEL && !_isAdminLoggedIn.value) {
            showMessage("রুট সুরক্ষিত: '/admin' এক্সেস করতে এডমিন প্রমাণীকরণ প্রয়োজন।", isError = true)
            CurrentScreen.ADMIN_LOGIN
        } else {
            screen
        }

        if (_currentScreen.value != targetScreen) {
            screenStack.push(_currentScreen.value)
            _currentScreen.value = targetScreen
        }
    }

    fun navigateToRoute(route: String) {
        val screen = CurrentScreen.fromRoute(route)
        navigateTo(screen)
    }

    fun navigateBack(): Boolean {
        if (!screenStack.isEmpty()) {
            _currentScreen.value = screenStack.pop()
            return true
        }
        if (_currentScreen.value != CurrentScreen.HOME) {
            _currentScreen.value = CurrentScreen.HOME
            return true
        }
        return false
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        navigateTo(CurrentScreen.PRODUCT_DETAIL)
    }

    fun openOrderTracking(order: Order) {
        _selectedOrder.value = order
        navigateTo(CurrentScreen.ORDER_TRACKING)
    }

    fun filterByCategory(categorySlug: String?) {
        _filters.value = _filters.value.copy(categorySlug = categorySlug)
        navigateTo(CurrentScreen.CATALOG)
    }

    fun filterByBrand(brandName: String?) {
        _filters.value = _filters.value.copy(brandName = brandName)
        navigateTo(CurrentScreen.CATALOG)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilters(newFilters: FilterState) {
        _filters.value = newFilters
    }

    fun resetFilters() {
        _filters.value = FilterState()
        _searchQuery.value = ""
    }

    // Cart Actions
    fun addToCart(product: Product, quantity: Int = 1, size: String? = null, color: String? = null) {
        viewModelScope.launch {
            if (product.stock <= 0) {
                showMessage("Sorry, this item is currently out of stock.", isError = true)
                return@launch
            }
            repository.addToCart(product, quantity, size, color)
            showMessage("Added \"${product.name}\" to your cart!")
        }
    }

    fun updateCartQuantity(cartId: String, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartId, qty)
        }
    }

    fun removeFromCart(cartId: String) {
        viewModelScope.launch {
            repository.removeFromCart(cartId)
            showMessage("Item removed from cart.")
        }
    }

    fun toggleSaveForLater(cartId: String) {
        viewModelScope.launch {
            repository.toggleSaveForLater(cartId)
        }
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val subtotal = cartSubtotal.value
            val coupon = repository.validateCoupon(code, subtotal)
            if (coupon != null) {
                _appliedCoupon.value = coupon
                showMessage("Coupon ${coupon.code} applied! Enjoy your discount.")
            } else {
                showMessage("Invalid or ineligible promo code for this order.", isError = true)
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        showMessage("Coupon removed.")
    }

    // Wishlist
    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val added = repository.toggleWishlist(product)
            if (added) {
                showMessage("Saved to your wishlist!")
            } else {
                showMessage("Removed from your wishlist.")
            }
        }
    }

    fun moveWishlistItemToCart(item: WishlistItem) {
        viewModelScope.launch {
            val product = repository.getProductById(item.productId)
            if (product != null) {
                repository.addToCart(product, 1)
                repository.toggleWishlist(product)
                showMessage("Moved \"${product.name}\" to cart!")
            }
        }
    }

    // Checkout & Order Placement
    fun placeOrder(
        shippingAddress: ShippingAddress,
        deliveryMethod: String,
        paymentMethodName: String,
        onSuccess: (Order) -> Unit
    ) {
        viewModelScope.launch {
            val activeCart = cartItems.value.filter { !it.savedForLater }
            if (activeCart.isEmpty()) {
                showMessage("Your cart is empty.", isError = true)
                return@launch
            }

            val orderItems = activeCart.map {
                OrderItem(
                    productId = it.productId,
                    productName = it.productName,
                    quantity = it.quantity,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    size = it.selectedSize,
                    color = it.selectedColor
                )
            }

            val orderId = "AT-${(100000..999999).random()}"
            val now = System.currentTimeMillis()
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            val timeline = listOf(
                OrderTimelineEvent(
                    title = "Order Placed",
                    description = "Received order via $paymentMethodName on ${sdf.format(Date(now))}",
                    timestamp = now,
                    isCompleted = true
                ),
                OrderTimelineEvent(
                    title = "Order Confirmed",
                    description = "Verified inventory and payment gateway approval",
                    timestamp = now + 1000,
                    isCompleted = true
                ),
                OrderTimelineEvent(
                    title = "Processing & Quality Check",
                    description = "Packaging at AT Logistics Hub",
                    timestamp = now + 3600000,
                    isCompleted = false
                ),
                OrderTimelineEvent(
                    title = "Out for Delivery",
                    description = "Courier handover with tracking ID AT-EXP-${orderId}",
                    timestamp = now + (24 * 3600000),
                    isCompleted = false
                ),
                OrderTimelineEvent(
                    title = "Delivered",
                    description = "Signature upon parcel delivery",
                    timestamp = now + (48 * 3600000),
                    isCompleted = false
                )
            )

            val order = Order(
                orderId = orderId,
                customerId = "cust_alex",
                customerName = shippingAddress.fullName,
                customerEmail = "alex.mercer@example.com",
                customerPhone = shippingAddress.phone,
                items = orderItems,
                subtotal = cartSubtotal.value,
                discount = cartDiscount.value,
                shippingFee = cartShippingFee.value,
                tax = cartTax.value,
                grandTotal = cartGrandTotal.value,
                shippingAddress = shippingAddress,
                paymentMethod = paymentMethodName,
                paymentStatus = if (paymentMethodName.contains("Cash")) "PENDING" else "PAID",
                orderStatus = "CONFIRMED",
                createdAt = now,
                timeline = timeline
            )

            // Save order & clear active cart items
            repository.createOrder(order)
            repository.clearCart()
            _appliedCoupon.value = null
            _selectedOrder.value = order
            showMessage("Order #$orderId placed successfully!")
            onSuccess(order)
        }
    }

    // Reviews
    fun submitReview(productId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            if (comment.isBlank()) {
                showMessage("Please enter your review feedback.", isError = true)
                return@launch
            }
            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val review = Review(
                id = "rev_${System.currentTimeMillis()}",
                productId = productId,
                userName = _userProfile.value.fullName,
                rating = rating,
                comment = comment.trim(),
                date = sdf.format(Date()),
                isVerifiedPurchase = true
            )
            repository.addReview(review)
            showMessage("Thank you! Your verified review has been published.")
        }
    }

    fun getProductReviews(productId: String): Flow<List<Review>> {
        return repository.getReviews(productId)
    }

    // Addresses
    fun saveAddress(address: ShippingAddress) {
        viewModelScope.launch {
            repository.saveAddress(address)
            showMessage("Shipping address saved.")
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            repository.deleteAddress(id)
            showMessage("Address deleted.")
        }
    }

    // Contact messages
    fun sendContactMessage(name: String, email: String, subject: String, message: String) {
        viewModelScope.launch {
            repository.sendMessage(name, email, subject, message)
            showMessage("Message sent! Our support team will reply within 24 hours.")
        }
    }

    // Admin Operations
    fun toggleUserRole() {
        _currentUserRole.value = if (_currentUserRole.value == "Super Admin") "Customer" else "Super Admin"
        showMessage("Active role switched to: ${_currentUserRole.value}")
    }

    fun adminSaveProduct(product: Product) {
        viewModelScope.launch {
            repository.insertOrUpdateProduct(product, _currentUserRole.value)
            showMessage("Product \"${product.name}\" saved successfully.")
        }
    }

    fun adminDeleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id, _currentUserRole.value)
            showMessage("Product deleted.")
        }
    }

    fun adminAdjustStock(id: String, newStock: Int, reason: String) {
        viewModelScope.launch {
            repository.updateStock(id, newStock, reason, _currentUserRole.value)
            showMessage("Stock adjusted to $newStock units.")
        }
    }

    fun adminUpdateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, _currentUserRole.value)
            showMessage("Order #$orderId marked as $newStatus.")
        }
    }

    fun adminCreateCoupon(coupon: Coupon) {
        viewModelScope.launch {
            repository.insertCoupon(coupon)
            showMessage("Coupon ${coupon.code} created!")
        }
    }

    fun adminUpdateSettings(settings: StoreSettings) {
        _storeSettings.value = settings
        showMessage("Store settings updated successfully.")
    }

    fun adminUpdateCMS(bannerContent: BannerContent) {
        _bannerContent.value = bannerContent
        showMessage("Homepage CMS & promotional banners updated.")
    }

    fun adminUpdateMessageStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateMessageStatus(id, status)
            showMessage("Message updated to $status.")
        }
    }

    private fun showMessage(msg: String, isError: Boolean = false) {
        _uiMessage.value = UiMessage(message = msg, isError = isError)
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }
}
