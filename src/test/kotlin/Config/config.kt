package Config

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

// This could be made as external library for usage in other projects later
class Config (private val driver: WebDriver) {
    private val loadWait: WebDriverWait = WebDriverWait(driver, Duration.ofSeconds(10))

    fun untilInvisibilityOfElementLocated(locator: By) {
        loadWait.until(ExpectedConditions.invisibilityOfElementLocated(locator))
    }
    fun untilFrameToBeAvailableAndSwitchToIt(locator: By) {
        loadWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator))
    }
    fun untilVisibilityOfElementLocated(locator: By) {
        loadWait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }
    fun untilVisibilityOfNestedElementsLocated(parent: WebElement, locator: By): WebElement {
        val visibleElements = loadWait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(parent, locator))
        return visibleElements[0]
    }
    fun untilElementToBeClickable(locator: By) {
        loadWait.until(ExpectedConditions.elementToBeClickable(locator))
    }
    fun untilElementToBeClickableAndClicks(locator: By) {
        loadWait.until(ExpectedConditions.elementToBeClickable(locator)).click()
    }
    fun untilPresenceOfElementLocated(locator: By) {
        loadWait.until(ExpectedConditions.presenceOfElementLocated(locator))
    }
    fun untilNumberOfElementsToBeMoreThan(locator: By, value: String) {
        loadWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator,value.toInt()))
    }

    fun findElement(locator: By):WebElement {
        return driver.findElement(locator)
    }
    fun findElements(locator: By):List<WebElement> {
       return driver.findElements(locator)
    }
    fun findElementAndClick(locator: By) {
        driver.findElement(locator).click()
    }
    fun findElementAndSendKeys(locator: By, keys: CharSequence) {
        driver.findElement(locator).sendKeys(keys)
    }
    fun findElementAndReturnString(locator: By):String {
        return driver.findElement(locator).text
    }
    fun findElementAndReturnTrimmedString(locator: By):String {
        return driver.findElement(locator).text.trim()
    }
    fun findElementAndSelectByVisibleText(locator: By, keys: String) {
        Select(driver.findElement(locator)).selectByVisibleText(keys)
    }
    fun moveSlider(locator: WebElement, offset: Int) {
        return Actions(driver).clickAndHold(locator).moveByOffset(offset, 0).release().perform()
    }
    fun findAndMoveSlider (locator: By, offset: Int){
        val element = findElement(locator)
        moveSlider(element, offset)
    }
    fun getShippingNameForRadio(radioElement: WebElement): String{
        val shippingId = radioElement.getAttribute("id") ?: throw AssertionError("Selected radio has no ID")
        val selectedShippingName = By.cssSelector("label[for='$shippingId'] span.h6.carrier-name")
        return findElementAndReturnTrimmedString(selectedShippingName)
    }
    fun getPaymentNameForRadio(radioElement: WebElement): String{
        val purchaseId = radioElement.getAttribute("id") ?: throw AssertionError("Selected radio has no ID")
        val selectedPurchaseName = By.cssSelector("label[for='$purchaseId']")
        return findElementAndReturnTrimmedString(selectedPurchaseName)
    }
    fun getActualShippingMethod(orderShippingElement: By): Pair<String, String> {
        val shippingElement = findElement(orderShippingElement)
        val secondPartShippingText = shippingElement.findElement(By.tagName("em")).text.trim()
        val fullShippingElementText = shippingElement.text.trim()
        val firstPartShippingText = fullShippingElementText.replace(secondPartShippingText, "").trim()
        val actualShippingMethod = firstPartShippingText.split(":").lastOrNull()?.trim() ?: ""
        return  Pair(actualShippingMethod, firstPartShippingText)
    }
    fun getFinalPaymentMethod(paymentText: String): String{
       val finalPaymentMethod =  paymentText.removeSuffix(" (COD)").removeSuffix(" transfer").removePrefix("Payment method: ")
            .removePrefix("Payment method: Payments by ").removePrefix("Payments by ")
        return finalPaymentMethod
    }
}





