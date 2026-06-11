package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By heading = By.xpath("//h1[text()='Ferramentas disponíveis']");
    private final By endDateLabel = By.xpath("//label[text()='Data de devolução']");
    private final By endDateInput = By.xpath("//input[@type='date']");
    private final By simulateButton = By.xpath("//button[text()='Simular valor']");
    private final By goToRentalsButton = By.xpath("//button[text()='Ir para locações']");
    private final By errorMessage = By.className("error-text");
    private final By simulationResult = By.className("query-result");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void setEndDate(String date) {
        driver.findElement(endDateInput).sendKeys(date);
    }

    public void clickSimulate() {
        driver.findElement(simulateButton).click();
    }

    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    public String getSimulationResultText() {
        return driver.findElement(simulationResult).getText();
    }

    public boolean isSimulationValueVisible() {
        return !driver.findElements(simulationResult).isEmpty();
    }

    public boolean isHeadingVisible() {
        return driver.findElement(heading).isDisplayed();
    }

    public boolean isEndDateInputVisible() {
        return driver.findElement(endDateLabel).isDisplayed() && driver.findElement(endDateInput).isDisplayed();
    }

    public boolean isSimulateButtonVisible() {
        return driver.findElement(simulateButton).isDisplayed();
    }

    public boolean isGoToRentalsButtonVisible() {
        return driver.findElement(goToRentalsButton).isDisplayed();
    }

    public void clickGoToRentals() {
        driver.findElement(goToRentalsButton).click();
    }

    public void clickMaintenanceOfFirstTool() {
        driver.findElement(By.xpath("//tr[contains(@class, 'tool-row')][1]//button[text()='Manutenção']")).click();
    }

    public void clickFirstToolRow() {
        driver.findElement(By.className("tool-row")).click();
    }

    public boolean isFirstToolRowSelected() {
        return driver.findElement(By.className("tool-row")).getAttribute("class").contains("tool-row--selected");
    }

    public boolean isToolTableVisible() {
        return !driver.findElements(By.cssSelector("tbody tr")).isEmpty();
    }
}
