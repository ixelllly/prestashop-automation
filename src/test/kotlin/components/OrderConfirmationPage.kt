package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import Config.*

class OrderConfirmationPage(driver: WebDriver) {
    private val config = Config(driver)
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
        config.untilVisibilityOfElementLocated(orderConfirmationPage)


        val referenceText = config.findElementAndReturnTrimmedString(orderReferenceElement)
        if (referenceText.isEmpty()) {
            throw AssertionError("Order reference not found")
        }
        println(referenceText)

        // Payment method assertion
        val paymentText = config.findElementAndReturnTrimmedString(orderPaymentElement)
        if (paymentText.isEmpty()) {
            throw AssertionError("Order payment method not found")
        }

        val finalPaymentMethod = config.getFinalPaymentMethod(paymentText)
        assert(finalPaymentMethod.equals(expectedPaymentMethod, ignoreCase = true)) {
            "Payment method mismatch! Expected: '$expectedPaymentMethod', but found: '$finalPaymentMethod' (full text: '$paymentText')"
        }
        println("Payment method: $finalPaymentMethod (matches expected)")
    }

    // Order method assertion
    private fun checkOrderDetailsShipping(expectedShippingMethod: String) {
        val shippingElementText = config.findElementAndReturnTrimmedString(orderShippingElement)
        if (shippingElementText.isEmpty()) {
            throw AssertionError("Order shipping method not found")
        }

        val (actualShippingMethod, firstPartShippingText) = config.getActualShippingMethod(orderShippingElement)
        assert(actualShippingMethod == expectedShippingMethod) {
            "Shipping method mismatch! Expected: '$expectedShippingMethod', but found: '$actualShippingMethod' (full text: '$firstPartShippingText')"
        }
        println("Shipping method: $actualShippingMethod (matches expected)")
    }
}