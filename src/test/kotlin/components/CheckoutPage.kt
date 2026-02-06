package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import kotlin.math.absoluteValue
import Config.*

class CheckoutPage(driver: WebDriver) {
    private val config = Config(driver)

    // Address form (step 1: Addresses)
    private val addressForm: By = By.id("field-address1")  // Confirm addresses section
    private val cityForm: By = By.id("field-city")  // Confirm city section
    private val countryForm: By = By.id("field-id_country") // Country element
    private val vatNumberForm: By = By.id("field-vat_number")// Vat element
    private val postCodeForm: By = By.id("field-postcode") // Post code element
    private val continueToShipping: By = By.name("confirm-addresses")  // confirmation

    // Shipping method (step 2: Shipping)
    private val shippingMethods: By =
        By.cssSelector(".delivery-options .custom-radio input[type='radio']")  // Radio buttons for shipping methods
    private val continueToPayment: By = By.name("confirmDeliveryOption")  // Delivery confirmation
    private val shippingExtraContent: By = By.cssSelector(".row.carrier-extra-content.js-carrier-extra-content")


    // Payment (step 3: Payment)
    private val paymentByCheck: By =
        By.cssSelector(".payment-options .custom-radio input[type='radio']")// Radio buttons
    private val termsCheckbox: By = By.id("conditions_to_approve[terms-and-conditions]")  // Agree to terms
    private val cartTotalPaymentElement: By =
        By.cssSelector(".cart-summary-totals .value") // Carts total amount summary
    private val payByCheckTotalPaymentElement: By =
        By.cssSelector("#payment-option-2-additional-information dd:nth-of-type(1)")  // Total payment in shipping method
    private val shippingCostElement: By = By.cssSelector("#cart-subtotal-shipping .value") // Shipping cost

    // Confirm order (step 4: Confirm)
    private val placeOrderButton: By = By.cssSelector("#payment-confirmation button")  // Place Order

    // Shipping method if none then defaults to radio button 1 = 'My carrier'
    private val shippingButtonIndex = System.getProperty("shipping.method", "1").toInt() //Shipping method index 0-1
    private val paymentButtonIndex = System.getProperty("payment.method", "1").toInt() // Payment method index 0-2

    // Runs the full sequence
    fun checkoutFormFill(expectedTotal: Double): Pair<String, String> {
        println("Checkout page")
        fillAddressForm()
        val selectedShippingName = chooseShippingMethod()
        val selectedPaymentName = choosePaymentOption()
        verifyTotal(expectedTotal)
        confirmOrder()
        return Pair(selectedShippingName, selectedPaymentName)
    }

    // Fill out the address form
    private fun fillAddressForm() {
        config.untilVisibilityOfElementLocated(addressForm)
        config.findElementAndSendKeys(addressForm, "Test address")
        config.findElementAndSendKeys(cityForm, "Paris")
        config.findElementAndSelectByVisibleText(countryForm, "France")
        config.untilVisibilityOfElementLocated(vatNumberForm)
        config.findElementAndSendKeys(postCodeForm, "75001")
        config.findElementAndClick(continueToShipping)
    }

    // Choose a shipping method
    private fun chooseShippingMethod(): String {
        config.untilPresenceOfElementLocated(shippingMethods)
        val radios = config.findElements(shippingMethods)
        if (radios.isNotEmpty()) {
            println("Found ${radios.size} shipping options")
        }
        if (radios.size != 2) {
            throw AssertionError("Expected exactly 2 shipping options, but found ${radios.size}")
        }

        // Handles selection of shipping method
        if (radios[shippingButtonIndex].isSelected) {
            println("Pick up in-store is already selected")
        } else {
            println("Clicking 'My carrier' (second radio button)")
            radios[1].click()
            config.untilInvisibilityOfElementLocated(shippingExtraContent)
        }

        val selectedRadio = radios.first { it.isSelected }
        val selectedShippingName = config.getShippingNameForRadio(selectedRadio)
        println("Selected shipping method: $selectedShippingName")

        config.findElementAndClick(continueToPayment)

        return selectedShippingName
    }

    private fun choosePaymentOption(): String {
        config.untilPresenceOfElementLocated(paymentByCheck)

        val radios = config.findElements(paymentByCheck)
        if (radios.isNotEmpty()) {
            println("Found ${radios.size} payment options")
        }
        if (radios.size != 3) {
            throw AssertionError("Expected exactly 3 payment options, but found ${radios.size}")
        }

        // Handle selection of payment method
        val indexToClick = when (paymentButtonIndex) {
            0 -> {
                println("Pay by Cash on Delivery selected")
                0
            }

            1 -> {
                println("Pay by Check selected")
                1
            }

            2 -> {
                println("Pay by bank wire selected")
                2
            }

            else -> {
                println("Invalid payment index $paymentButtonIndex; defaulting to 'Pay by Check' (index 1)")
                1
            }
        }

        radios[indexToClick].click()

        val selectedRadio = radios.first { it.isSelected }
        val selectedPaymentName =
            config.getPaymentNameForRadio(selectedRadio).removePrefix("Pay by ").removeSuffix(" wire")
        println("Selected payment method: $selectedPaymentName")

        return selectedPaymentName
    }

    // Choose payment method - "Payment by Check", check total price
    private fun verifyTotal(expectedTotal: Double) {
        config.untilVisibilityOfElementLocated(cartTotalPaymentElement)

        val totalPaymentValue = if (paymentButtonIndex == 1) {
            config.findElementAndReturnString(payByCheckTotalPaymentElement)
                .replace("€", "")
                .replace("(tax incl.)", "")
                .trim()
                .toDoubleOrNull() ?: throw AssertionError("Could not read total payment - tax including ")
        } else {
            config.findElementAndReturnString(cartTotalPaymentElement)
                .replace("€", "")
                .replace("(tax incl.)", "")
                .trim()
                .toDoubleOrNull() ?: throw AssertionError("Could not read total payment - tax including ")
        }

        val shippingCostValue = config.findElementAndReturnTrimmedString(shippingCostElement)
            .replace("€", "")
            .trim()
            .toDoubleOrNull() ?: 0.0

        val adjustedExpectedTotalValue = expectedTotal + shippingCostValue

        // Assertion of total payment value to payment with shipping matches
        val delta = 0.01
        assert((totalPaymentValue - adjustedExpectedTotalValue).absoluteValue <= delta) {
            "Payment total calculation failed! Expected: eur ${adjustedExpectedTotalValue}, but shows: eur ${totalPaymentValue}"
        }
        println("Payment with shipping eur $adjustedExpectedTotalValue matches eur $totalPaymentValue")

        config.findElementAndClick(termsCheckbox)
    }

    // Confirm order
    private fun confirmOrder() {
        config.untilElementToBeClickableAndClicks(placeOrderButton)
    }
}