package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class HeaderPage extends BasePage {
    private final By hamburgerButton = By.className("header-hamburger");
    private final By menuNav = By.className("header-nav");
    private final By logoutButton = By.className("header-logout");

    public HeaderPage(WebDriver driver) {
        super(driver);
    }

    public void clickHamburger() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(hamburgerButton));
    }

    public boolean isMenuOpen() {
        return driver.findElement(menuNav).getAttribute("class").contains("header-nav--open");
    }

    public boolean isMenuHidden() {
        return !isMenuOpen();
    }

    public void clickHome() {
        driver.findElement(By.linkText("Home")).click();
    }

    public void clickTools() {
        driver.findElement(By.linkText("Ferramentas")).click();
    }

    public void clickRental() {
        driver.findElement(By.linkText("Locações")).click();
    }

    public void clickHistory() { driver.findElement(By.linkText("Histórico")).click();}

    public void clickLogout() {
        driver.findElement(logoutButton).click();
    }
}
