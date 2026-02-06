package core

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

class Application {
    private lateinit var driver: WebDriver

    fun openBaseUrl(baseUrl: String) {
        WebDriverManager.chromedriver().setup()
        driver = ChromeDriver()
        driver.manage().window().maximize()
        driver.get(baseUrl)
    }

    fun getDriver(): WebDriver {
        return driver

    }

    fun close() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }
}