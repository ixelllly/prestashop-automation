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
    testImplementation("org.seleniumhq.selenium:selenium-java:4.36.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.3.3")
    testImplementation("org.testng:testng:7.10.2")
}

tasks.test {
    useTestNG {
        suites("src/test/kotlin/test/resources/prestashop-suite.xml")
        useDefaultListeners = true
    }
    testLogging {
        events("passed", "failed", "skipped")
    }
}
kotlin {
    jvmToolchain(21)
}