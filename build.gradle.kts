plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.10.0")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.36.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.3.3")
    testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:1.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("base.url", "https://demo.prestashop.com/")
    systemProperty("target.min.price", "18") // Filter min range
    systemProperty("target.max.price", "23") // Filter max range
    systemProperty("expected.product.count", "3") // Change only when product count after filtration is known
    systemProperty("expected.product.quantity", "-1") // Whatever value your heart desires
    systemProperty("shipping.method", "1") // 0-1 value choices - 'Click and collect' = 0, 'My carrier' = 1
    systemProperty("payment.method", "1") // 0-2 value choices - 'Pay by Cash on Delivery' = 0, 'Payment by Check' = 1, 'Pay by bank wire' = 2
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
    }
}
kotlin {
    jvmToolchain(21)
}