package test

import org.openqa.selenium.WebDriver
import core.Application
import components.*
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Parameters
import org.testng.annotations.Test
import java.math.BigDecimal

class PrestaShopTest {
    private lateinit var driver: WebDriver
    private val app = Application()
    private lateinit var baseUrl: String
    private lateinit var targetMinPrice: BigDecimal
    private lateinit var targetMaxPrice: BigDecimal
    private var expectedProductCount: Int = 0
    private var expectedProductQuantity: Int = 0
    private var shippingMethodIndex: Int = 0
    private var paymentMethodIndex: Int = 0

    @BeforeClass
    @Parameters(
        "baseUrl",
        "targetMinPrice",
        "targetMaxPrice",
        "expectedProductCount",
        "expectedProductQuantity",
        "shippingMethodIndex",
        "paymentMethodIndex"
    )
    fun setUp(
        baseUrl: String,
        targetMinPrice: String,
        targetMaxPrice: String,
        expectedProductCount: String,
        expectedProductQuantity: String,
        shippingMethodIndex: String,
        paymentMethodIndex: String
    ) {
        this.baseUrl = baseUrl
        this.targetMinPrice = targetMinPrice.toBigDecimal()
        this.targetMaxPrice = targetMaxPrice.toBigDecimal()
        this.expectedProductCount = expectedProductCount.toInt()
        this.expectedProductQuantity = expectedProductQuantity.toInt()
        this.shippingMethodIndex = shippingMethodIndex.toInt()
        this.paymentMethodIndex = paymentMethodIndex.toInt()

        app.openBaseUrl(baseUrl)
        driver = app.getDriver()
    }

    @AfterClass
    fun tearDown() {
        app.close()
    }

    @Test
    fun testPrestaShopScenario() {
        // User registration
        RegistrationPage(driver).register()

        // Open Accessories section, filter by price, verify prices, choose random product
        val accessoriesPage = AccessoriesPage(driver, targetMinPrice, targetMaxPrice, expectedProductCount)
        accessoriesPage.accessoriesFilterVerifyChooseRandomProduct()

        // Add product quantity set in.env to cart and continue shopping, get subtotal
        val productPage = ProductPage(driver, expectedProductQuantity)
        val (subtotal1, normalisedQuantity) = productPage.addVerifyProductToCart()

        // Re filter and choose another random product
        accessoriesPage.accessoriesFilterVerifyChooseRandomProduct()

        // Add one more item to cart, verify accumulative total in modal, then proceed to cart
        productPage.addProductToCart(normalisedQuantity)
        val subtotal2 = productPage.productPriceCalculation(normalisedQuantity, subtotal1)
        productPage.proceedToCheckoutMoral()

        // Calculate and verify grand total in cart page
        val cartPage = CartPage(driver)
        val expectedTotal = cartPage.cartAmountVerification(subtotal1, subtotal2)

        //  Fill out the form and confirm order
        val checkoutPage = CheckoutPage(driver, shippingMethodIndex, paymentMethodIndex)
        val (selectedShippingMethod, selectedPaymentMethod) = checkoutPage.checkoutFormFill(expectedTotal)

        // Check order details on confirmation page
        val confirmationPage = OrderConfirmationPage(driver)
        confirmationPage.confirmationPage(selectedShippingMethod, selectedPaymentMethod)

        // Logout
        Logout(driver).logout()
    }
}