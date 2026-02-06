package components

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import kotlin.random.Random
import Config.*

class AccessoriesPage(driver: WebDriver) {
    private val config = Config(driver)

    // Dropdown of accessories and subcategory - home accessories
    private val accessories: By = By.cssSelector("#category-6 a.dropdown-item")
    private val homeAccessories: By = By.cssSelector("#subcategories li:nth-child(2)")
    private val sliderVisibility: By = By.cssSelector(".faceted-slider") // slider visibility

    // Left and Right sliders for filtering with max and min amounts
    private val sliderLeft: By = By.cssSelector((".ui-slider-handle:nth-of-type(1)")) // Left slider
    private val sliderRight: By = By.cssSelector((".ui-slider-handle:nth-of-type(2)")) // Right slider
    private val sliderMaxMin: By = By.cssSelector("ul[data-slider-min][data-slider-max]") // Both sliders min and max

    // web spinner...
    private val loading: By = By.cssSelector(".spinner") // fidget spinner loading

    // Products
    private val productLocator: By = By.cssSelector(".product-miniature") // Product
    private val productLink: By = By.cssSelector("a.thumbnail.product-thumbnail") // Product thumbnail
    private val productPrice: By = By.cssSelector(".product-price-and-shipping") // Product price

    // Runs the full sequence
    fun accessoriesFilterVerifyChooseRandomProduct() {
        println("Home accessories page")
        goToAccessories()
        filterPrice()
        verifyFilteredProducts()
        chooseRandomProduct()
    }

    // Fetch min/max
    private fun getSliderMinMax(): Pair<Float, Float> {
        config.untilVisibilityOfElementLocated(sliderMaxMin)
        val sliderElement = config.findElement(sliderMaxMin)
        val minStr = sliderElement.getAttribute("data-slider-min")
        val maxStr = sliderElement.getAttribute("data-slider-max")

        val minPrice = minStr?.toFloatOrNull() ?: throw IllegalArgumentException("Invalid data-slider-min: $minStr")
        val maxPrice = maxStr?.toFloatOrNull() ?: throw IllegalArgumentException("Invalid data-slider-max: $maxStr")

        return Pair(minPrice, maxPrice)
    }

    // User navigates to home accessory section
    private fun goToAccessories() {
        config.findElementAndClick(accessories)
        config.untilElementToBeClickableAndClicks(homeAccessories)
        config.untilVisibilityOfElementLocated(sliderVisibility)
    }

    // Calculation and movement of sliders to apply filter
    private fun filterPrice() {
        // Fetch min/max + targets
        val (minP, maxP) = getSliderMinMax()
        val targetMin = System.getProperty("target.min.price", "18").toFloat()
        val targetMax = System.getProperty("target.max.price", "23").toFloat()

        val sliderBarLeft = config.findElement(sliderLeft)
        val sliderBarRight = config.findElement(sliderRight)
        val sliderWidth = sliderBarRight.location.x - sliderBarLeft.location.x

        val percentageMin = (targetMin - minP) / (maxP - minP)
        val percentageMax = (targetMax - minP) / (maxP - minP)

        val minOffset = (sliderWidth * percentageMin).toInt()
        val maxOffset = (sliderWidth * percentageMax).toInt() - sliderWidth

        config.moveSlider(sliderBarLeft, minOffset)
        config.untilInvisibilityOfElementLocated(loading)

        config.findAndMoveSlider(sliderRight, maxOffset)
        config.untilInvisibilityOfElementLocated(loading)
    }

    // Verification of filtered products if they are in defined price range
    private fun verifyFilteredProducts() {
        // Fetch target price from .env
        val targetMin = System.getProperty("target.min.price", "18").toFloat()
        val targetMax = System.getProperty("target.max.price", "23").toFloat()
        val expectedCount = System.getProperty("expected.product.count", "3").toInt()

        // Wait for products (already in filterPrice()
        config.untilNumberOfElementsToBeMoreThan(productLocator, "0")

        val product = config.findElements(productLocator)
        // Assert count (for sanity)
        assert(product.size == expectedCount) {
            "Expected $expectedCount in range [$targetMin, $targetMax], but found ${product.size}. Check filter or site changes."
        }
        println("Expected product count $expectedCount corresponds to found product count after filtering ${product.size}")


        // Check each of filtered product prices
        product.forEach { productPrices ->
            val priceText = config.untilVisibilityOfNestedElementsLocated(productPrices, productPrice).text
                .replace("€", "")
                .trim()
                .toFloatOrNull() ?: throw AssertionError("Invalid price format in product")

            assert(priceText in targetMin..targetMax) {
                "Product price $priceText is outside range [$targetMin, $targetMax]. Verify product data or filter."
            }
            println("eur $priceText is in the range eur $targetMin, eur$targetMax")
        }
    }

    private fun chooseRandomProduct() {
        // Re fetch products (post filter)
        val product = config.findElements(productLocator)
        if (product.isEmpty()) throw AssertionError("No products found post filter. Check earlier steps.")

        // Random product pick
        val randomIndex = Random.nextInt(0, product.size)
        val selectedProduct = product[randomIndex]

        // Click to details page
        selectedProduct.findElement(productLink).click()
    }
}