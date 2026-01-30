package core

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

class Application {
    private lateinit var driver: WebDriver

    fun openBaseUrl() {
        WebDriverManager.chromedriver().setup()
        driver = ChromeDriver()
        driver.manage().window().maximize()
        val baseUrl = System.getProperty("base.url", "https://demo.prestashop.com/")
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