package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class OrderConfirmationPage(private val driver: WebDriver) {
    private val orderConfirmationPage: By = By.id("content-hook_order_confirmation")  // Confirmation section
    private val orderReferenceElement: By = By.id("order-reference-value")  // Order ref text
    private val orderPaymentElement: By = By.cssSelector("#order-details li:nth-of-type(2)") // Payment method
    private val orderShippingElement: By = By.cssSelector("#order-details li:nth-of-type(3)") // Shipping method

    // Runs the full sequence
    fun confirmationPage(expectedShippingMethod: String, expectedPaymentMethod: String) {
        println("Confirmation page")
        checkOrderDetailsPayment(expectedPaymentMethod)
        checkOrderDetailsShipping(expectedShippingMethod)
    }

    // Check order details on confirmation page
    private fun checkOrderDetailsPayment(expectedPaymentMethod: String) {
        WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.visibilityOfElementLocated(
                orderConfirmationPage
            )
        )

        val referenceText = driver.findElement(orderReferenceElement).text.trim()
        if (referenceText.isEmpty()) {
            throw AssertionError("Order reference not found")
        }
        println(referenceText)

        // Payment method assertion
        val paymentText = driver.findElement(orderPaymentElement).text.trim()
        if (paymentText.isEmpty()) {
            throw AssertionError("Order payment method not found")
        }

        driver.findElement(orderPaymentElement).text.trim()
        val finalPaymentMethod =
            paymentText.removeSuffix(" (COD)").removeSuffix(" transfer").removePrefix("Payment method: ")
                .removePrefix("Payment method: Payments by ").removePrefix("Payments by ")
        assert(finalPaymentMethod.equals(expectedPaymentMethod, ignoreCase = true)) {
            "Payment method mismatch! Expected: '$expectedPaymentMethod', but found: '$finalPaymentMethod' (full text: '$paymentText')"
        }
        println("Payment method: $finalPaymentMethod (matches expected)")
    }

    // Order method assertion
    private fun checkOrderDetailsShipping(expectedShippingMethod: String) {
        val shippingElementText = driver.findElement(orderShippingElement).text.trim()
        if (shippingElementText.isEmpty()) {
            throw AssertionError("Order shipping method not found")
        }
        val shippingElement = driver.findElement(orderShippingElement)
        val secondPartShippingText = shippingElement.findElement(By.tagName("em")).text.trim()
        val fullShippingElementtext = shippingElement.text.trim()
        val firstPartShippingText = fullShippingElementtext.replace(secondPartShippingText, "").trim()

        val actualShippingMethod = firstPartShippingText.split(":").lastOrNull()?.trim() ?: ""
        assert(actualShippingMethod == expectedShippingMethod) {
            "Shipping method mismatch! Expected: '$expectedShippingMethod', but found: '$actualShippingMethod' (full text: '$firstPartShippingText')"
        }
        println("Shipping method: $actualShippingMethod (matches expected)")
    }
}