package au.jamal.instabiobot.instagram

import au.jamal.instabiobot.utilities.Log
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URI
import java.time.Duration
import java.util.logging.Level
import kotlin.system.exitProcess

class BrowserManager(production: Boolean, debug: Boolean, timeout: Long) {

    private val options: ChromeOptions = configureOptions(debug)
    val browser: WebDriver = startBrowser(options, production, timeout)
    val wait = WebDriverWait(browser, Duration.ofSeconds(timeout))

    fun end() {
        Log.warn("Killing Selenium session...")
        try {
            browser.close()
            browser.quit()
            Log.status("Selenium session ended...")
        } catch (e: Exception) {
            Log.alert("Failed to kill selenium driver")
        }
    }

    private fun configureOptions(debug: Boolean): ChromeOptions {
        val options = ChromeOptions()
        if (!debug) {
            options.addArguments("--headless")
            options.addArguments("--disable-logging")
            options.setExperimentalOption("excludeSwitches", listOf("enable-automation"))
            java.util.logging.Logger.getLogger("org.openqa.selenium").level = Level.OFF
        } else {
            Log.warn("Running in debug mode...")
        }
        return options
    }

    private fun startBrowser(options: ChromeOptions, production: Boolean, timeout: Long): WebDriver {
        val browser: WebDriver = try {
            if (production) {
                val remoteDriver = RemoteWebDriver(URI.create("http://selenium:4444/wd/hub").toURL(), options)
                Log.status("Started production browser session")
                remoteDriver
            } else {
                val localDriver = ChromeDriver(options)
                Log.warn("Running in local mode...")
                localDriver
            }
        } catch (e: Exception) {
            Log.alert("Failed to start browser...")
            Log.error(e)
            exitProcess(0)
        }
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout))
        return browser
    }

}