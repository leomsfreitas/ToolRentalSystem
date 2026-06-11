package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ToolsPage extends BasePage {
    private final By heading = By.xpath("//h1[text()='Ferramentas']");
    private final By nameInput = By.xpath("//input[@placeholder='Nome']");
    private final By dailyRateInput = By.xpath("//input[@placeholder='Diária (R$)']");
    private final By weeklyRateInput = By.xpath("//input[@placeholder='Semanal (R$)']");
    private final By monthlyRateInput = By.xpath("//input[@placeholder='Mensal (R$)']");
    private final By registerButton = By.xpath("//button[text()='Cadastrar']");
    
    private final By nameHeader = By.xpath("//th[text()='Nome']");
    private final By statusHeader = By.xpath("//th[text()='Status']");
    private final By dailyHeader = By.xpath("//th[text()='Diária']");
    private final By weeklyHeader = By.xpath("//th[text()='Semanal']");
    private final By monthlyHeader = By.xpath("//th[text()='Mensal']");

    public ToolsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isHeadingVisible() {
        return driver.findElement(heading).isDisplayed();
    }

    public boolean isNameInputVisible() {
        return driver.findElement(nameInput).isDisplayed();
    }

    public boolean isDailyRateInputVisible() {
        return driver.findElement(dailyRateInput).isDisplayed();
    }

    public boolean isWeeklyRateInputVisible() {
        return driver.findElement(weeklyRateInput).isDisplayed();
    }

    public boolean isMonthlyRateInputVisible() {
        return driver.findElement(monthlyRateInput).isDisplayed();
    }

    public boolean isRegisterButtonVisible() {
        return driver.findElement(registerButton).isDisplayed();
    }

    public boolean areTableHeadersVisible() {
        return driver.findElement(nameHeader).isDisplayed() &&
               driver.findElement(statusHeader).isDisplayed() &&
               driver.findElement(dailyHeader).isDisplayed() &&
               driver.findElement(weeklyHeader).isDisplayed() &&
               driver.findElement(monthlyHeader).isDisplayed();
    }

    public void fillForm(String name, String daily, String weekly, String monthly) {
        driver.findElement(nameInput).sendKeys(name);
        driver.findElement(dailyRateInput).sendKeys(daily);
        driver.findElement(weeklyRateInput).sendKeys(weekly);
        driver.findElement(monthlyRateInput).sendKeys(monthly);
    }

    public void clickRegister() {
        driver.findElement(registerButton).click();
    }

    public boolean isToolInTable(String name) {
        return !driver.findElements(By.xpath("//td[text()='" + name + "']")).isEmpty();
    }

    public String getErrorMessage() {
        return driver.findElement(By.xpath("//form//p")).getText();
    }

    public boolean isErrorMessageVisible() {
        return !driver.findElements(By.xpath("//form//p")).isEmpty();
    }
}
