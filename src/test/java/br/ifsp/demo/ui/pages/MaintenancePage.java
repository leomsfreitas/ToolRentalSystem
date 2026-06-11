package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class MaintenancePage extends BasePage {
    private final By heading = By.xpath("//h1[text()='Manutenção']");
    private final By availableTab = By.xpath("//button[text()='Disponíveis']");
    private final By activeMaintenanceTab = By.xpath("//button[text()='Manutenções ativas']");
    private final By sendToMaintenanceButton = By.xpath("//button[text()='Enviar para manutenção']");
    private final By returnFromMaintenanceButton = By.xpath("//button[text()='Registrar retorno']");

    public MaintenancePage(WebDriver driver) {
        super(driver);
    }

    public boolean isHeadingVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        return driver.findElement(heading).isDisplayed();
    }

    public boolean isAvailableTabVisible() {
        return !driver.findElements(availableTab).isEmpty();
    }

    public boolean isActiveMaintenanceTabVisible() {
        return !driver.findElements(activeMaintenanceTab).isEmpty();
    }

    public void clickActiveMaintenanceTab() {
        driver.findElement(activeMaintenanceTab).click();
    }

    public boolean isSendToMaintenanceButtonVisible() {
        return !driver.findElements(sendToMaintenanceButton).isEmpty();
    }

    public void clickSendToMaintenanceButton() {
        driver.findElement(sendToMaintenanceButton).click();
    }

    public int getAvailableToolCount() {
        return driver.findElements(sendToMaintenanceButton).size();
    }

    public boolean isReturnFromMaintenanceButtonVisible() {
        return !driver.findElements(returnFromMaintenanceButton).isEmpty();
    }

    public void clickReturnFromMaintenanceButton() {
        driver.findElement(returnFromMaintenanceButton).click();
    }

    public int getMaintenanceToolCount() {
        return driver.findElements(returnFromMaintenanceButton).size();
    }

    public boolean isSelectedRowVisible() {
        return !driver.findElements(By.className("row-selected")).isEmpty();
    }

    public int getSelectedRowCount() {
        return driver.findElements(By.className("row-selected")).size();
    }
}
