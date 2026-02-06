package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import kotlin.math.absoluteValue
import Config.*

class ProductPage(driver: WebDriver) {
    private val config = Config(driver)
    private val quantity = System.getProperty("expected.product.quantity", "3").toInt()

    // Price of product
    private val currentPrice: By = By.cssSelector(".current-price-value")

    private val quantityUp: By = By.cssSelector(".touchspin-up")  // + button
    private val buttonAddToCart: By = By.cssSelector("button.add-to-cart")
    private val currentPriceValue: By = By.cssSelector(".current-price-value")
    private val productTotalValue: By = By.cssSelector(".product-total .value")
    private val cartContent: By = By.cssSelector(".cart-content")

    private val continueShoping: By = By.cssSelector("button[type='button'][class='btn btn-secondary']")
    private val proceedToCheckout: By = By.cssSelector(".cart-content-btn a.btn.btn-primary")

    // Runs the full sequence
    fun addVerifyProductToCart(): Pair<Double, Int> {
        println("Product page")
        val normalizedQuantity = normalizeQuantity()
        addProductToCart(normalizedQuantity)
        val subtotal = productPriceCalculation(normalizedQuantity)
        continueShoppingMoral()
        return Pair(subtotal, normalizedQuantity)
    }

    fun addProductToCart(normalizedQuantity: Int = 3) {
        config.untilVisibilityOfElementLocated(currentPrice)
        // Increase quantity (click + button 'quantity-1' times as default value starts at 1)
        repeat(normalizedQuantity - 1) {
            config.findElementAndClick(quantityUp)
        }

        // Add to cart
        config.findElementAndClick(buttonAddToCart)
        config.untilVisibilityOfElementLocated(cartContent)
    }

    fun productPriceCalculation(normalizedQuantity: Int = 3, previousTotal: Double = 0.0): Double {
        // Get unit price
        val unitPriceText = config.findElementAndReturnString(currentPriceValue)
            .replace("€", "")
            .trim()
            .toDoubleOrNull() ?: throw AssertionError("Invalid units price")

        // Get total unit price
        val displayedTotalText = config.findElementAndReturnString(productTotalValue)
            .replace("€", "")
            .trim()
            .toDoubleOrNull() ?: throw AssertionError("Could not read cart item subtotal")

        // Check if price is correctly calculated
        val itemSubtotal = unitPriceText * normalizedQuantity
        val expectedTotal = itemSubtotal + previousTotal
        val delta = 0.01
        assert(Math.abs(displayedTotalText - expectedTotal).absoluteValue <= delta) {
            "Price calculation failed! Expected: eur ${expectedTotal}, but cart shows: eur ${displayedTotalText}"
        }
        println("Total for products eur $expectedTotal correctly calculated")

        return itemSubtotal
    }

    private fun continueShoppingMoral() {
        try {
            config.untilElementToBeClickable(continueShoping)
            config.findElementAndClick(continueShoping)
        } catch (e: Exception) {
            // modal already closed or not present
        }
        config.untilInvisibilityOfElementLocated(cartContent)
    }

    fun proceedToCheckoutMoral() {
        try {
            config.untilElementToBeClickable(proceedToCheckout)
            config.findElementAndClick(proceedToCheckout)
        } catch (e: Exception) {
            // modal already closed or not present
        }
        config.untilInvisibilityOfElementLocated(cartContent)
    }

    private fun normalizeQuantity(): Int {
        return if (quantity < 1) {
            println("Quantity is invalid $quantity. Defaulting to 1")
            1
        } else {
            quantity
        }
    }
}
