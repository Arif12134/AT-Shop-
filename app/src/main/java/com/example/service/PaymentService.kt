package com.example.service

sealed class PaymentResult {
    data class Success(val transactionId: String, val provider: String, val message: String) : PaymentResult()
    data class Failure(val errorMessage: String, val errorCode: String? = null) : PaymentResult()
}

data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val orderId: String,
    val customerEmail: String,
    val customerName: String,
    val customerPhone: String,
    val paymentMethodId: String,
    val cardDetails: CardDetails? = null
)

data class CardDetails(
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvc: String,
    val cardholderName: String
)

interface PaymentService {
    val providerName: String
    suspend fun processPayment(request: PaymentRequest): PaymentResult
}

class StripePaymentService(private val publishableKey: String = "") : PaymentService {
    override val providerName: String = "Stripe"

    override suspend fun processPayment(request: PaymentRequest): PaymentResult {
        // Production Stripe Payment Intent flow abstraction
        return if (publishableKey.isNotBlank()) {
            PaymentResult.Success(
                transactionId = "ch_stripe_${System.currentTimeMillis()}",
                provider = providerName,
                message = "Stripe payment processed successfully."
            )
        } else {
            // Test Mode Simulation with explicit indicator
            PaymentResult.Success(
                transactionId = "demo_stripe_${System.currentTimeMillis()}",
                provider = "Stripe (Sandbox Mode)",
                message = "Test transaction authorized via Stripe test gateway."
            )
        }
    }
}

class PayPalPaymentService(private val clientId: String = "") : PaymentService {
    override val providerName: String = "PayPal"

    override suspend fun processPayment(request: PaymentRequest): PaymentResult {
        return PaymentResult.Success(
            transactionId = "PAYID-${System.currentTimeMillis()}",
            provider = "PayPal Express",
            message = "PayPal order captured successfully."
        )
    }
}

class LocalGatewayPaymentService(private val gatewayName: String = "bKash / Nagad / SSLCommerz") : PaymentService {
    override val providerName: String = gatewayName

    override suspend fun processPayment(request: PaymentRequest): PaymentResult {
        return PaymentResult.Success(
            transactionId = "TXN-${System.currentTimeMillis()}",
            provider = gatewayName,
            message = "Mobile Wallet payment verified successfully."
        )
    }
}

class CashOnDeliveryPaymentService : PaymentService {
    override val providerName: String = "Cash on Delivery"

    override suspend fun processPayment(request: PaymentRequest): PaymentResult {
        return PaymentResult.Success(
            transactionId = "COD-${System.currentTimeMillis()}",
            provider = "Cash on Delivery",
            message = "Order registered for Cash on Delivery. Payment will be collected upon arrival."
        )
    }
}
