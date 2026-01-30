package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class RegistrationPage(private val driver: WebDriver) {
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

    companion object {
        fun onRegistrationPage(driver: WebDriver): RegistrationPage {
            return RegistrationPage(driver)
        }
    }

    // Method to perform registration
    fun register() {
        val loadWait = WebDriverWait(driver, Duration.ofSeconds(20))

        // Navigate to registration
        loadWait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator))
        loadWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeLocator))
        loadWait.until(ExpectedConditions.visibilityOfElementLocated(loginLink))
        driver.findElement(loginLink).click()
        driver.findElement(noAccountLink).click()

        // Check if key fields are displayed
        if (!driver.findElement(emailField).isDisplayed || !driver.findElement(passwordField).isDisplayed) {
            throw NoSuchElementException("Registration form sequence has changed")
        }

        driver.findElement(firstNameField).sendKeys("Test")
        driver.findElement(lastNameField).sendKeys("User")
        val email = "test${System.currentTimeMillis()}@example.com"
        driver.findElement(emailField).sendKeys(email)
        val password = "Test${System.currentTimeMillis()}123-"
        driver.findElement(passwordField).sendKeys(password)
        driver.findElement(customerPrivacyCheckbox).click()
        driver.findElement(psgdprCheckbox).click()
        driver.findElement(submitButton).click()

        // Wait and check if user is logged in after registration
        loadWait.until(ExpectedConditions.visibilityOfElementLocated(accountInfo))
    }
}