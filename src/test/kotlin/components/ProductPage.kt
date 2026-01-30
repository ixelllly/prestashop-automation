package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import kotlin.math.absoluteValue

class ProductPage(private val driver: WebDriver) {
    // Price of product
    private val currentPrice: By = By.cssSelector(".current-price-value")

    private val quantityUp: By = By.cssSelector(".touchspin-up")  // + button
    private val buttonAddToCart: By = By.cssSelector("button.add-to-cart")
    private val currentPriceValue: By = By.cssSelector(".current-price-value")
    private val productTotalValue: By = By.cssSelector(".product-total .value")
    private val cartContent: By = By.cssSelector(".cart-content")

    private val continueShoping: By = By.cssSelector("button[type='button'][class='btn btn-secondary']")
    private val proceedToCheckout: By = By.cssSelector(".cart-content-btn a.btn.btn-primary")

    companion object {
        fun onProductPage(driver: WebDriver): ProductPage {
            return ProductPage(driver)
        }
    }

    // Runs the full sequence
    fun addVerifyProductToCart(quantity: Int = 3): Double {
        println("Product page")
        addProductToCart(quantity)
        val subtotal = productPriceCalculation(quantity)
        continueShoppingMoral()
        return subtotal
    }

    fun addProductToCart(quantity: Int = 3) {
        WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(currentPrice))
        // Increase quantity (click + button 'quantity-1' times as default value starts at 1)
        if (quantity > 1) {
            repeat(quantity - 1) {
                driver.findElement(quantityUp).click()
            }
        }

        // Add to cart
        driver.findElement(buttonAddToCart).click()
        WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(cartContent))
    }

    fun productPriceCalculation(quantity: Int = 3, previousTotal: Double = 0.0): Double {
        // Get unit price
        val unitPriceText = driver.findElement(currentPriceValue).text
            .replace("€", "")
            .trim()
            .toDoubleOrNull() ?: throw AssertionError("Invalid units price")

        // Get total unit price
        val displayedTotalText = driver.findElement(productTotalValue).text
            .replace("€", "")
            .trim()
            .toDoubleOrNull() ?: throw AssertionError("Could not read cart item subtotal")

        // Check if price is correctly calculated
        val itemSubtotal = unitPriceText * quantity
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
            WebDriverWait(
                driver,
                Duration.ofSeconds(10)
            ).until(ExpectedConditions.elementToBeClickable(continueShoping))
            driver.findElement(continueShoping).click()
        } catch (e: Exception) {
            // modal already closed or not present
        }
        WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        ).until(ExpectedConditions.invisibilityOfElementLocated(cartContent))
    }

    fun proceedToCheckoutMoral() {
        try {
            WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.elementToBeClickable(
                    proceedToCheckout
                )
            )
            driver.findElement(proceedToCheckout).click()
        } catch (e: Exception) {
            // modal already closed or not present
        }
        WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        ).until(ExpectedConditions.invisibilityOfElementLocated(cartContent))
    }
}
