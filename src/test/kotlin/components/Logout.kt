package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import Config.*

class Logout(driver: WebDriver) {
    private val config = Config(driver)
    private val logout: By = By.cssSelector(".logout.hidden-sm-down") // Logout element
    private val signIn: By = By.cssSelector("#_desktop_user_info .user-info a") // Sign in element

    fun logout() {
        config.findElementAndClick(logout)
        config.untilVisibilityOfElementLocated(signIn)
        println("User logged out")
    }
}