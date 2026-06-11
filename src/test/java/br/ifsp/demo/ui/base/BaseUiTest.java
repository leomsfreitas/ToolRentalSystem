package br.ifsp.demo.ui.base;

import br.ifsp.demo.ui.pages.LoginPage;
import br.ifsp.demo.ui.pages.RegisterPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;

import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseUiTest {

    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void registerUser(String name, String lastname, String email, String password) {
        driver.get("http://localhost:5173/register");
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.fillForm(name, lastname, email, password);
        registerPage.clickRegister();
        
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().equals("http://localhost:5173/"));
    }

    protected void login(String email, String password) {
        driver.get("http://localhost:5173/");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail(email);
        loginPage.fillPassword(password);
        loginPage.clickLogin();
        
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().contains("/home"));
    }

    protected void createTool(String name, double daily, double weekly, double monthly) {
        String toolJson = String.format(Locale.US, "{\"name\": \"%s\", \"dailyRate\": %.2f, \"weeklyDailyRate\": %.2f, \"monthlyDailyRate\": %.2f}", name, daily, weekly, monthly);
        ((JavascriptExecutor) driver).executeScript(
            "fetch('http://localhost:8080/api/v1/tools', { " +
            "  method: 'POST', " +
            "  headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + localStorage.getItem('token') }, " +
            "  body: '" + toolJson + "' " +
            "});"
        );
    }

    protected void createCustomer(String name, String email) {
        String customerJson = String.format("{\"name\": \"%s\"}", name);
        ((JavascriptExecutor) driver).executeScript(
            "fetch('http://localhost:8080/api/v1/customers', { " +
            "  method: 'POST', " +
            "  headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + localStorage.getItem('token') }, " +
            "  body: '" + customerJson + "' " +
            "});"
        );
    }

    protected String createToolAndGetId(String name, double daily, double weekly, double monthly) {
        String toolJson = String.format(Locale.US,
            "{\"name\":\"%s\",\"dailyRate\":%.2f,\"weeklyDailyRate\":%.2f,\"monthlyDailyRate\":%.2f}",
            name, daily, weekly, monthly
        );
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
            "var callback = arguments[arguments.length - 1];" +
            "fetch('http://localhost:8080/api/v1/tools', {" +
            "  method: 'POST'," +
            "  headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + localStorage.getItem('token')}," +
            "  body: '" + toolJson + "'" +
            "}).then(function(r) { return r.json(); })" +
            ".then(function(d) { callback(d.id); })" +
            ".catch(function() { callback(null); });"
        );
        return result != null ? result.toString() : null;
    }

    protected void sendToolToMaintenanceViaApi(String toolId) {
        ((JavascriptExecutor) driver).executeAsyncScript(
            "var callback = arguments[arguments.length - 1];" +
            "fetch('http://localhost:8080/api/v1/maintenance/send/" + toolId + "', {" +
            "  method: 'POST'," +
            "  headers: {'Authorization': 'Bearer ' + localStorage.getItem('token')}" +
            "}).then(function() { callback(true); }).catch(function() { callback(false); });"
        );
    }
}
