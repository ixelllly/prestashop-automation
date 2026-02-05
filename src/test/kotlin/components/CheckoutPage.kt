package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import kotlin.math.absoluteValue

class CheckoutPage(private val driver: WebDriver) {
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
        WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(addressForm))
        driver.findElement(addressForm).sendKeys("Test Address")
        driver.findElement(cityForm).sendKeys("Paris")
        Select(driver.findElement(countryForm)).selectByVisibleText("France")
        WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        ).until(ExpectedConditions.visibilityOfElementLocated(vatNumberForm))
        driver.findElement(postCodeForm).sendKeys("75001")
        driver.findElement(continueToShipping).click()
    }

    // Choose a shipping method
    private fun chooseShippingMethod(): String {
        WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        ).until(ExpectedConditions.presenceOfElementLocated(shippingMethods))
        val radios = driver.findElements(shippingMethods)
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
            WebDriverWait(driver, Duration.ofSeconds(10)).until {
                driver.findElement(By.cssSelector(".row.carrier-extra-content.js-carrier-extra-content"))
                    .getCssValue("display") == "none"
            }
        }
        val selectedRadio = radios.first { it.isSelected } ?: throw AssertionError("No shipping method selected")
        val shippingId = selectedRadio.getAttribute("id") ?: throw AssertionError("Selected radio has no ID")
        val selectedShippingName =
            driver.findElement(By.cssSelector("label[for='$shippingId'] span.h6.carrier-name")).text.trim()
        println("Selected shipping method: $selectedShippingName")

        driver.findElement(continueToPayment).click()

        return selectedShippingName
    }

    private fun choosePaymentOption(): String {
        WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(paymentByCheck))

        val radios = driver.findElements(paymentByCheck)
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

        val selectedRadio = radios.first { it.isSelected } ?: throw AssertionError("No payment method selected")
        val purchaseId = selectedRadio.getAttribute("id") ?: throw AssertionError("Selected radio has no ID")
        val selectedPaymentName =
            driver.findElement(By.cssSelector("label[for='$purchaseId']")).text.trim().removePrefix("Pay by ")
                .removeSuffix(" wire")
        println("Selected payment method: $selectedPaymentName")

        return selectedPaymentName
    }

    // Choose payment method - "Payment by Check", check total price
    private fun verifyTotal(expectedTotal: Double) {
        WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.visibilityOfElementLocated(
                cartTotalPaymentElement
            )
        )

        val totalPaymentValue = if (paymentButtonIndex == 1) {
            driver.findElement(payByCheckTotalPaymentElement).text
                .replace("€", "")
                .replace("(tax incl.)", "")
                .trim()
                .toDoubleOrNull() ?: throw AssertionError("Could not read total payment - tax including ")
        } else {
            driver.findElement(cartTotalPaymentElement).text
                .replace("€", "")
                .replace("(tax incl.)", "")
                .trim()
                .toDoubleOrNull() ?: throw AssertionError("Could not read total payment - tax including ")
        }

        val shippingCostValue = driver.findElement(shippingCostElement).text
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
        driver.findElement(termsCheckbox).click()
    }

    // Confirm order
    private fun confirmOrder() {
        WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(placeOrderButton))
        driver.findElement(placeOrderButton).click()
    }

}