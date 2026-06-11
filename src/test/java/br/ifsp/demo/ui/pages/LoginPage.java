package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By emailInput = By.xpath("//input[@placeholder='Email']");
    private final By passwordInput = By.xpath("//input[@placeholder='Senha']");
    private final By loginButton = By.xpath("//button[text()='Entrar']");
    private final By errorMessage = By.xpath("//p[text()='Email ou senha inválidos.']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void fillEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
    }

    public void fillPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public boolean isErrorMessageVisible() {
        return !driver.findElements(errorMessage).isEmpty() && driver.findElement(errorMessage).isDisplayed();
    }
}
