package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import Config.*

class RegistrationPage(driver: WebDriver) {
    private val config = Config(driver)
    private val email = "test${System.currentTimeMillis()}@example.com"
    private val password = "Test${System.currentTimeMillis()}123-"

    // Element defining as properties
    private val loaderLocator = By.id("loadingMessage")
    private val iframeLocator: By = By.id("framelive")
    private val loginLink: By = By.cssSelector("#_desktop_user_info .user-info a")
    private val noAccountLink: By = By.cssSelector(".no-account a")
    private val firstNameField: By = By.id("field-firstname")
    private val lastNameField: By = By.id("field-lastname")
    private val emailField: By = By.id("field-email")
    private val passwordField: By = By.id("field-password")
    private val customerPrivacyCheckbox: By = By.name("customer_privacy")
    private val psgdprCheckbox: By = By.name("psgdpr")
    private val submitButton: By = By.cssSelector("button[type='submit'][data-link-action='save-customer']")
    private val accountInfo: By = By.cssSelector(".user-info .account")

    // Method to perform registration
    fun register() {
        // Navigate to registration
        config.untilInvisibilityOfElementLocated(loaderLocator)
        config.untilFrameToBeAvailableAndSwitchToIt(iframeLocator)
        config.untilVisibilityOfElementLocated(loginLink)
        config.findElementAndClick(loginLink)
        config.findElementAndClick(noAccountLink)

        // Check if key fields are displayed
        if (!config.findElement(emailField).isDisplayed || !config.findElement(passwordField).isDisplayed) {
            throw NoSuchElementException("Registration form sequence has changed")
        }

        config.findElementAndSendKeys(firstNameField, "Test")
        config.findElementAndSendKeys(lastNameField, "User")
        config.findElementAndSendKeys(emailField, email)
        config.findElementAndSendKeys(passwordField, password)
        config.findElementAndClick(customerPrivacyCheckbox)
        config.findElementAndClick(psgdprCheckbox)
        config.findElementAndClick(submitButton)

        // Wait and check if user is logged in after registration
        config.untilVisibilityOfElementLocated(accountInfo)
    }
}