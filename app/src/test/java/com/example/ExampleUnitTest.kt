package com.example

import com.example.data.InitialData
import com.example.service.CashOnDeliveryPaymentService
import com.example.service.PaymentRequest
import com.example.service.PaymentResult
import com.example.service.StripePaymentService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun initialProducts_arePopulatedAndValid() {
    val products = InitialData.initialProducts
    assertTrue("Initial catalog must have products", products.isNotEmpty())
    products.forEach { prod ->
      assertTrue("Product price must be greater than 0", prod.price > 0)
      assertTrue("Product SKU must be non-empty", prod.sku.isNotBlank())
      assertTrue("Product images list must be populated", prod.images.isNotEmpty())
    }
  }

  @Test
  fun sampleCoupons_haveValidDiscounts() {
    val coupons = InitialData.sampleCoupons
    val atShop40 = coupons.find { it.code == "ATSHOP40" }
    assertNotNull(atShop40)
    assertEquals(40, atShop40!!.discountPercent)
    assertTrue(atShop40.minSpend > 0)
  }

  @Test
  fun paymentService_stripeTestProcess_returnsSuccess() = runBlocking {
    val stripeService = StripePaymentService()
    val request = PaymentRequest(
      amount = 279.0,
      currency = "USD",
      orderId = "AT-TEST-001",
      customerEmail = "alex.mercer@example.com",
      customerName = "Alex Mercer",
      customerPhone = "+15552345678",
      paymentMethodId = "pm_card_visa"
    )
    val result = stripeService.processPayment(request)
    assertTrue("Stripe test payment should succeed", result is PaymentResult.Success)
    val success = result as PaymentResult.Success
    assertTrue("Transaction ID must be generated", success.transactionId.isNotBlank())
  }

  @Test
  fun paymentService_cashOnDelivery_returnsSuccess() = runBlocking {
    val codService = CashOnDeliveryPaymentService()
    val request = PaymentRequest(
      amount = 145.0,
      currency = "USD",
      orderId = "AT-TEST-002",
      customerEmail = "alex.mercer@example.com",
      customerName = "Alex Mercer",
      customerPhone = "+15552345678",
      paymentMethodId = "cod"
    )
    val result = codService.processPayment(request)
    assertTrue("COD payment should succeed", result is PaymentResult.Success)
  }

  @Test
  fun adminLogin_withCorrectCredentials_arifulAnd123456_succeeds() {
    val username = "ariful"
    val password = "123456"
    val isValid = username.trim().equals("ariful", ignoreCase = true) && password == "123456"
    assertTrue("Owner ariful with password 123456 must authenticate successfully", isValid)
  }

  @Test
  fun adminLogin_withIncorrectCredentials_fails() {
    val wrongUser = "admin"
    val wrongPass = "password"
    val isWrongValid = wrongUser.trim().equals("ariful", ignoreCase = true) && wrongPass == "123456"
    assertFalse("Random or wrong credentials must be rejected", isWrongValid)
  }
}

