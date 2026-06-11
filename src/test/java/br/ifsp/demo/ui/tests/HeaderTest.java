package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.HeaderPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderTest extends BaseUiTest {
    private HeaderPage headerPage;

    @BeforeEach
    public void setup() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        driver.get("http://localhost:5173/");
        driver.findElement(By.xpath("//input[@placeholder='Email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@placeholder='Senha']")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Entrar']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().contains("/home"));
        headerPage = new HeaderPage(driver);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Header should render navigation links and logout button")
    public void shouldRenderHeaderNavigationAndLogoutButton() {
        assertThat(driver.findElement(By.linkText("Home")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.linkText("Ferramentas")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.linkText("Locações")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.linkText("Manutenções")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.linkText("Histórico")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.linkText("Clientes")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.xpath("//button[text()='Sair']")).isDisplayed()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Header menu should open and close correctly")
    public void shouldOpenAndCloseTheHamburgerMenu() {
        driver.manage().window().setSize(new Dimension(320, 812));
        
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        assertThat(headerPage.isMenuHidden()).isTrue();

        headerPage.clickHamburger();

        wait.until(d -> headerPage.isMenuOpen());

        headerPage.clickHamburger();

        wait.until(d -> headerPage.isMenuHidden());
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Header navigation should route to the home page")
    public void shouldNavigateToHomeWhenHomeLinkIsClicked() {
        headerPage.clickHome();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().contains("/home"));
        assertThat(driver.getCurrentUrl()).contains("/home");
        assertThat(driver.findElement(By.xpath("//h1[text()='Ferramentas disponíveis']")).isDisplayed()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Logout should clear the session and redirect to login")
    public void shouldLogoutAndRedirectToLogin() {
        headerPage.clickLogout();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().equals("http://localhost:5173/"));
        assertThat((String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('token');")).isNull();
        assertThat(driver.getCurrentUrl()).isEqualTo("http://localhost:5173/");
        assertThat(driver.findElement(By.xpath("//h1[text()='Login']")).isDisplayed()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Header should collapse navigation on small screens")
    public void shouldCollapseNavigationOnSmallViewport() {
        driver.manage().window().setSize(new Dimension(320, 812));
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        assertThat(headerPage.isMenuHidden()).isTrue();
        headerPage.clickHamburger();
        wait.until(d -> headerPage.isMenuOpen());
        headerPage.clickHamburger();
        wait.until(d -> headerPage.isMenuHidden());
    }
}
