# PrestaShop Automation Project

## This project automates testing on demo.prestashop.com using **Kotlin, Selenium, and Gradle.**

## To Run from IDE (IntelliJ):
- Right-click PrestaShopTestSuite.kt in src/test/kotlin > Run 'PrestaShopTestSuite'.
- Or use Gradle: In the Gradle tool window (right sidebar), expand Tasks > verification > double-click 'test'.

## To Run from Command Line (PowerShell or Terminal):
- Navigate to the project folder: cd C:\Users\YourUsername\Downloads\prestashop_automation or any other folder where you saved it.
- For a full/initial test run: ./gradlew test
- For repeated runs: ./gradlew test --tests PrestaShopTestSuite

## Dependencies
- See build.gradle.kts

### Personal notes
While working on this project, I got a bit carried away and implemented some unnecessary bonus features. Mostly because I have never used Kotlin before.
I was learning the language on the fly while juggling Kotlin docs and homework while also really enjoying the no reload entire page - SPA :trollface:
Because of this experience I can definitely say that Kotlin > java 🤩
