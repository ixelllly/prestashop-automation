package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import kotlin.math.absoluteValue

class CartPage(private val driver: WebDriver) {
    // Cart summary page
    private val cartPageLocator: By = By.cssSelector(".cart-overview")  // Element to confirm cart page loaded
    private val cartTotalElement: By = By.cssSelector(".cart-total .value")  // Grand total (tax incl.)
    private val proceedToCheckoutButton: By = By.cssSelector(".checkout a.btn.btn-primary") // Proceed checkout

    companion object {
        fun onCartPage(driver: WebDriver): CartPage {
            return CartPage(driver)
        }
    }

    // Runs the full sequence
    fun cartAmountVerification(subtotal1: Double, subtotal2: Double): Double {
        println("Cart page")
        val expectedTotalValue = verifyTotalValue(subtotal1, subtotal2)
        proceedToCheckout()
        return expectedTotalValue
    }

    private fun verifyTotalValue(subtotal1: Double, subtotal2: Double): Double {
        // Wait for cart page to load
        WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.visibilityOfElementLocated(
                cartPageLocator
            )
        )
        val expectedTotal = subtotal1 + subtotal2

        // Get displayed grand total
        val cartTotalValue = driver.findElement(cartTotalElement).text
            .replace("€", "")
            .trim()
            .toDoubleOrNull() ?: throw AssertionError("Could not read cart grand total")

        // Assert matches expected sum (with delta for floating-point precision)
        val delta = 0.01
        assert((cartTotalValue - expectedTotal).absoluteValue <= delta) {
            "Grand total calculation failed! Expected: eur${expectedTotal}, but cart shows: eur${cartTotalValue}"
        }
        println("eur $expectedTotal matches eur $cartTotalValue")
        return expectedTotal
    }

    private fun proceedToCheckout() {
        WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.elementToBeClickable(
                proceedToCheckoutButton
            )
        )
        driver.findElement(proceedToCheckoutButton).click()
    }
}