package test

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriver
import core.Application
import components.*

class PrestaShopTest {
    private lateinit var driver: WebDriver
    private val app = Application()

    @BeforeEach
    fun setUp() {
        app.openBaseUrl()
        driver = app.getDriver()
    }

    @AfterEach
    fun tearDown() {
        app.close()
    }

    @Test
    fun testPrestaShopScenario() {
        // User registration
        RegistrationPage.onRegistrationPage(driver).register()

        // Open Accessories section, filter by price, verify prices, choose random product
        val accessoriesPage = AccessoriesPage.onAccessoriesPage(driver)
        accessoriesPage.accessoriesFilterVerifyChooseRandomProduct()

        // Add product quantity set in.env to cart and continue shopping, get subtotal
        val productPage = ProductPage.onProductPage(driver)
        val (subtotal1,normalisedQuantity) = productPage.addVerifyProductToCart()

        // Re filter and choose another random product
        accessoriesPage.accessoriesFilterVerifyChooseRandomProduct()

        // Add one more item to cart, verify accumulative total in modal, then proceed to cart
        productPage.addProductToCart(normalisedQuantity)
        val subtotal2 = productPage.productPriceCalculation(normalisedQuantity, subtotal1)
        productPage.proceedToCheckoutMoral()

        // Calculate and verify grand total in cart page
        val cartPage = CartPage.onCartPage(driver)
        val expectedTotal = cartPage.cartAmountVerification(subtotal1, subtotal2)

        //  Fill out the form and confirm order
        val checkoutPage = CheckoutPage.onCheckoutPage(driver)
        val (selectedShippingMethod, selectedPaymentMethod) = checkoutPage.checkoutFormFill(expectedTotal)

        // Check order details on confirmation page
        val confirmationPage = OrderConfirmationPage.onOrderConfirmationPage(driver)
        confirmationPage.confirmationPage(selectedShippingMethod, selectedPaymentMethod)

        // Logout
        Logout.onLogout(driver).logout()
    }
}