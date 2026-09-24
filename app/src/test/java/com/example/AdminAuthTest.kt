package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.AdminLoginResult
import com.example.data.AdminSession
import com.example.data.LoginField
import com.example.viewmodel.CurrentScreen
import com.example.viewmodel.ShopViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AdminAuthTest {

    private lateinit var app: Application
    private lateinit var viewModel: ShopViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        viewModel = ShopViewModel(app)
    }

    @Test
    fun routes_areCorrectlyMapped() {
        assertEquals("/admin/login", CurrentScreen.ADMIN_LOGIN.route)
        assertEquals("/admin", CurrentScreen.ADMIN_PANEL.route)
        assertEquals(CurrentScreen.ADMIN_LOGIN, CurrentScreen.fromRoute("/admin/login"))
        assertEquals(CurrentScreen.ADMIN_PANEL, CurrentScreen.fromRoute("/admin"))
    }

    @Test
    fun adminLogin_withEmptyUsername_returnsUsernameError() {
        val result = viewModel.attemptAdminLoginDetailed("", "123456")
        assertTrue(result is AdminLoginResult.Error)
        val error = result as AdminLoginResult.Error
        assertEquals(LoginField.USERNAME, error.field)
        assertFalse(viewModel.isAdminLoggedIn.value)
        assertNull(viewModel.adminSession.value)
    }

    @Test
    fun adminLogin_withEmptyPassword_returnsPasswordError() {
        val result = viewModel.attemptAdminLoginDetailed("ariful", "")
        assertTrue(result is AdminLoginResult.Error)
        val error = result as AdminLoginResult.Error
        assertEquals(LoginField.PASSWORD, error.field)
        assertFalse(viewModel.isAdminLoggedIn.value)
        assertNull(viewModel.adminSession.value)
    }

    @Test
    fun adminLogin_withInvalidCredentials_failsAndIncrementsFailedAttempts() {
        val initialAttempts = viewModel.failedLoginAttempts.value
        val result = viewModel.attemptAdminLoginDetailed("wrongUser", "wrongPass")
        assertTrue(result is AdminLoginResult.Error)
        assertEquals(initialAttempts + 1, viewModel.failedLoginAttempts.value)
        assertFalse(viewModel.isAdminLoggedIn.value)
        assertNull(viewModel.adminSession.value)
    }

    @Test
    fun adminLogin_withValidCredentials_succeedsAndEstablishesSession() {
        val result = viewModel.attemptAdminLoginDetailed("ariful", "123456")
        assertTrue("Login must succeed for ariful/123456", result is AdminLoginResult.Success)
        val success = result as AdminLoginResult.Success
        assertNotNull(success.session)
        assertEquals("ariful", success.session.username)
        assertTrue(success.session.isValid)
        assertTrue(viewModel.isAdminLoggedIn.value)
        assertNotNull(viewModel.adminSession.value)
        assertEquals(0, viewModel.failedLoginAttempts.value)
        // Should have navigated to ADMIN_PANEL
        assertEquals(CurrentScreen.ADMIN_PANEL, viewModel.currentScreen.value)
    }

    @Test
    fun adminPanelRoute_isProtectedWhenNotAuthenticated() {
        // Ensure logged out
        viewModel.adminLogout()
        assertFalse(viewModel.isAdminLoggedIn.value)

        // Attempt navigation directly to ADMIN_PANEL / '/admin'
        viewModel.navigateTo(CurrentScreen.ADMIN_PANEL)

        // Must be redirected to ADMIN_LOGIN
        assertEquals(CurrentScreen.ADMIN_LOGIN, viewModel.currentScreen.value)
    }

    @Test
    fun adminLogout_clearsSessionAndNavigatesToLogin() {
        // First login
        viewModel.attemptAdminLoginDetailed("ariful", "123456")
        assertTrue(viewModel.isAdminLoggedIn.value)

        // Then logout
        viewModel.adminLogout()
        assertFalse(viewModel.isAdminLoggedIn.value)
        assertNull(viewModel.adminSession.value)
        assertEquals(CurrentScreen.ADMIN_LOGIN, viewModel.currentScreen.value)
    }
}
