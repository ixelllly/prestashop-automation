package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class Logout(private val driver: WebDriver) {

    private val logout: By = By.cssSelector(".logout.hidden-sm-down") // Logout element
    private val signIn: By = By.cssSelector("#_desktop_user_info .user-info a") // Sign in element

    fun logout() {
        driver.findElement(logout).click()
        WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOfElementLocated(signIn))
        println("User logged out")
    }
}