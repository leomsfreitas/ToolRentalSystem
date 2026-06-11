package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterPage extends BasePage {

    private final By nameInput = By.xpath("//input[@placeholder='Nome']");
    private final By lastNameInput = By.xpath("//input[@placeholder='Sobrenome']");
    private final By emailInput = By.xpath("//input[@placeholder='Email']");
    private final By passwordInput = By.xpath("//input[@placeholder='Senha']");
    private final By registerButton = By.xpath("//button[text()='Cadastrar']");
    private final By errorMessage = By.xpath("//p[text()='Erro ao cadastrar. Verifique os dados e tente novamente.']");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public void fillForm(String name, String lastName, String email, String password) {
        driver.findElement(nameInput).sendKeys(name);
        driver.findElement(lastNameInput).sendKeys(lastName);
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickRegister() {
        driver.findElement(registerButton).click();
    }

    public boolean isErrorMessageVisible() {
        return !driver.findElements(errorMessage).isEmpty() && driver.findElement(errorMessage).isDisplayed();
    }
}
