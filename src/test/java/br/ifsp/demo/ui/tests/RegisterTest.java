package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterTest extends BaseUiTest {
    private RegisterPage registerPage;

    @BeforeEach
    public void setup() {
        registerPage = new RegisterPage(driver);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Register page should render the registration form")
    public void shouldRenderRegisterForm() {
        driver.get("http://localhost:5173/register");

        assertThat(driver.findElement(By.xpath("//h1[text()='Cadastro']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.xpath("//input[@placeholder='Nome']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.xpath("//input[@placeholder='Sobrenome']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.xpath("//input[@placeholder='Email']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.xpath("//input[@placeholder='Senha']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.xpath("//button[text()='Cadastrar']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.linkText("Entrar")).isDisplayed()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Valid registration should redirect the user to login")
    public void shouldRegisterAndRedirectToLoginWhenDataIsValid() {
        driver.get("http://localhost:5173/register");

        registerPage.fillForm(UiTestDataFactory.createName(), UiTestDataFactory.createLastName(), UiTestDataFactory.createEmail(), UiTestDataFactory.createPassword());
        registerPage.clickRegister();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().equals("http://localhost:5173/"));

        assertThat(driver.getCurrentUrl()).isEqualTo("http://localhost:5173/");
        assertThat(driver.findElements(By.xpath("//h1[text()='Cadastro']"))).isEmpty();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Invalid registration should show an error message")
    public void shouldShowErrorWhenRegistrationFails() {
        driver.get("http://localhost:5173/register");

        ((JavascriptExecutor) driver).executeScript(
            "window.fetch = function() { return Promise.resolve({ ok: false, status: 400 }); };"
        );

        registerPage.fillForm(UiTestDataFactory.createName(), UiTestDataFactory.createLastName(), UiTestDataFactory.createEmail(), UiTestDataFactory.createPassword());
        registerPage.clickRegister();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> registerPage.isErrorMessageVisible());

        assertThat(registerPage.isErrorMessageVisible()).isTrue();
        assertThat(driver.getCurrentUrl()).isEqualTo("http://localhost:5173/register");
    }
}
