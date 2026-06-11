package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CustomersPage extends BasePage {
    private final By heading = By.xpath("//h1[text()='Clientes']");
    private final By nameInput = By.xpath("//input[@placeholder='Nome do cliente']");
    private final By submitButton = By.xpath("//button[text()='Cadastrar']");
    private final By nameHeader = By.xpath("//th[text()='Nome']");
    private final By idHeader = By.xpath("//th[text()='ID']");

    public CustomersPage(WebDriver driver) {
        super(driver);
    }

    public boolean isHeadingVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        return driver.findElement(heading).isDisplayed();
    }

    public boolean isNameInputVisible() {
        return !driver.findElements(nameInput).isEmpty();
    }

    public boolean isSubmitButtonVisible() {
        return !driver.findElements(submitButton).isEmpty();
    }

    public boolean isNameHeaderVisible() {
        return !driver.findElements(nameHeader).isEmpty();
    }

    public boolean isIdHeaderVisible() {
        return !driver.findElements(idHeader).isEmpty();
    }

    public void fillName(String name) {
        driver.findElement(nameInput).sendKeys(name);
    }

    public void clickCadastrar() {
        driver.findElement(submitButton).click();
    }

    public boolean isCustomerInTable(String name) {
        return !driver.findElements(By.xpath("//td[text()='" + name + "']")).isEmpty();
    }

    public boolean isSubmitErrorVisible() {
        return !driver.findElements(By.xpath("//p[contains(@class,'error-text') and text()='Erro ao cadastrar cliente. Verifique o console.']")).isEmpty();
    }

    public boolean isLoadErrorVisible() {
        return !driver.findElements(By.xpath("//*[contains(text(),'Erro ao carregar clientes. Verifique o console.')]")).isEmpty();
    }

    public int getCustomerRowCount() {
        return driver.findElements(By.xpath("//tbody/tr[td[@class!=''] or not(contains(., 'Nenhum cliente'))]")).size();
    }

    public boolean hasNoCustomerRows() {
        return driver.findElements(By.xpath("//tbody/tr/td[not(contains(text(),'Nenhum cliente cadastrado.'))]")).isEmpty();
    }
}
