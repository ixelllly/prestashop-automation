package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import Config.*
import java.math.BigDecimal

class CartPage(driver: WebDriver) {
    private val config = Config(driver)

    // Cart summary page
    private val cartPageLocator: By = By.cssSelector(".cart-overview")  // Element to confirm cart page loaded
    private val cartTotalElement: By = By.cssSelector(".cart-total .value")  // Grand total (tax incl.)
    private val proceedToCheckoutButton: By = By.cssSelector(".checkout a.btn.btn-primary") // Proceed checkout

    // Runs the full sequence
    fun cartAmountVerification(subtotal1: BigDecimal, subtotal2: BigDecimal): BigDecimal {
        println("Cart page")
        val expectedTotalValue = verifyTotalValue(subtotal1, subtotal2)
        proceedToCheckout()
        return expectedTotalValue
    }

    private fun verifyTotalValue(subtotal1: BigDecimal, subtotal2: BigDecimal): BigDecimal {
        // Wait for cart page to load
        config.untilVisibilityOfElementLocated(cartPageLocator)

        val expectedTotal = subtotal1 + subtotal2

        // Get displayed grand total
        val cartTotalValue = config.findElementAndReturnString(cartTotalElement)
            .replace("€", "")
            .trim()
            .toBigDecimalOrNull() ?: throw AssertionError("Could not read cart grand total")

        // Assert matches expected sum
        assert(cartTotalValue.compareTo(expectedTotal) == 0) {
            "Grand total calculation failed! Expected: eur${expectedTotal}, but cart shows: eur${cartTotalValue}"
        }
        println("eur $expectedTotal matches eur $cartTotalValue")
        return expectedTotal
    }

    private fun proceedToCheckout() {
        config.untilElementToBeClickableAndClicks(proceedToCheckoutButton)
    }
}