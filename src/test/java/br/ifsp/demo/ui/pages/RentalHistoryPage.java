package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RentalHistoryPage extends BasePage {
    private final By heading = By.xpath("//h1[text()='Histórico']");
    private final By rentalsTab = By.xpath("//button[text()='Locações']");
    private final By maintenancesTab = By.xpath("//button[text()='Manutenções']");

    public RentalHistoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isHeadingVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        return driver.findElement(heading).isDisplayed();
    }

    public boolean isRentalsTabVisible() {
        return !driver.findElements(rentalsTab).isEmpty();
    }

    public boolean isMaintenancesTabVisible() {
        return !driver.findElements(maintenancesTab).isEmpty();
    }

    public void clickMaintenancesTab() {
        driver.findElement(maintenancesTab).click();
    }

    public void clickRentalsTab() {
        driver.findElement(rentalsTab).click();
    }

    public boolean isTableVisible() {
        return !driver.findElements(By.className("data-table")).isEmpty();
    }

    public boolean isRentalsEmptyStateVisible() {
        return !driver.findElements(By.xpath("//td[text()='Nenhuma locação encerrada.']")).isEmpty();
    }

    public boolean isMaintenancesEmptyStateVisible() {
        return !driver.findElements(By.xpath("//td[text()='Nenhuma manutenção encerrada.']")).isEmpty();
    }
}
