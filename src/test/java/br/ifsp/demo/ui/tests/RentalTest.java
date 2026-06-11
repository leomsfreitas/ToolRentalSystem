package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.HeaderPage;
import br.ifsp.demo.ui.pages.RentalPage;
import br.ifsp.demo.ui.pages.ToolsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class RentalTest extends BaseUiTest {

    @Test
    @Tag("UiTest")
    @DisplayName("Rental page should render the new rental form and active rentals tab")
    public void shouldRenderRentalPageWithNewRentalFormAndActiveRentalsTab() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/rental");
        
        RentalPage rentalPage = new RentalPage(driver);

        assertThat(rentalPage.isHeadingVisible()).isTrue();
        assertThat(rentalPage.isNewRentalTabVisible()).isTrue();
        assertThat(rentalPage.isActiveRentalsTabVisible()).isTrue();
        assertThat(rentalPage.isClientLabelVisible()).isTrue();
        assertThat(rentalPage.isGuaranteeLabelVisible()).isTrue();
        assertThat(rentalPage.isConfirmButtonVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Valid rental creation should show a success message")
    public void shouldCreateARentalWhenAValidCustomerToolAndGuaranteeAreSelected() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        createTool("Serra Circular", 20.0, 90.0, 250.0);
        createCustomer("Jose Silva", "jose@test.com");

        driver.get("http://localhost:5173/rental");

        RentalPage rentalPage = new RentalPage(driver);

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                !d.findElements(By.className("tool-row")).isEmpty());

        rentalPage.clickFirstToolRow();
        rentalPage.searchCustomer("Jose");
        rentalPage.selectCustomerFromDropdown("Jose Silva");
        rentalPage.selectGuarantee("Dinheiro");

        rentalPage.clickConfirm();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isSuccessMessageVisible());

        assertThat(rentalPage.getSuccessMessage()).isEqualTo("Locação registrada com sucesso.");
        assertThat(rentalPage.areToolsSelected()).isFalse();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Rental submission should require the minimum selections")
    public void shouldShowErrorWhenRentalIsSubmittedWithoutSelections() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        HeaderPage headerPage = new HeaderPage(driver);
        headerPage.clickRental();

        RentalPage rentalPage = new RentalPage(driver);
        rentalPage.clickConfirm();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isErrorMessageVisible());

        assertThat(rentalPage.getErrorMessage()).isEqualTo("Selecione ao menos uma ferramenta.");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Finalizing an active rental should show the total value")
    public void shouldFinalizeAnActiveRentalAndShowTheTotalValue() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/tools");
        ToolsPage toolsPage = new ToolsPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> toolsPage.isRegisterButtonVisible());
        toolsPage.fillForm("Serra Circular", "20", "90", "250");
        toolsPage.clickRegister();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> toolsPage.isToolInTable("Serra Circular"));

        driver.get("http://localhost:5173/customers");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                !d.findElements(By.xpath("//input[@placeholder='Nome do cliente']")).isEmpty());
        driver.findElement(By.xpath("//input[@placeholder='Nome do cliente']")).sendKeys("Cliente Finalize");
        driver.findElement(By.xpath("//button[text()='Cadastrar']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                !d.findElements(By.xpath("//td[text()='Cliente Finalize']")).isEmpty());

        HeaderPage headerPage = new HeaderPage(driver);
        headerPage.clickRental();

        RentalPage rentalPage = new RentalPage(driver);

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                !d.findElements(By.className("tool-row")).isEmpty());

        rentalPage.clickFirstToolRow();
        rentalPage.searchCustomer("Cliente");
        rentalPage.selectCustomerFromDropdown("Cliente Finalize");
        rentalPage.selectGuarantee("Dinheiro");
        rentalPage.clickConfirm();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isSuccessMessageVisible());

        rentalPage.clickActiveRentalsTab();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isFinalizeButtonVisible());
        assertThat(rentalPage.isFinalizeButtonVisible()).isTrue();

        int countBefore = rentalPage.getActiveRentalCount();

        rentalPage.clickFinalizeButton();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isSuccessMessageVisible());

        assertThat(rentalPage.getSuccessMessage()).startsWith("Locação finalizada. Valor total: R$");
        assertThat(rentalPage.getActiveRentalCount()).isLessThan(countBefore);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Canceling an active rental should refresh the active rentals list")
    public void shouldCancelAnActiveRentalAndRefreshTheList() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/tools");
        ToolsPage toolsPage = new ToolsPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> toolsPage.isRegisterButtonVisible());
        toolsPage.fillForm("Serra Elétrica", "20", "90", "250");
        toolsPage.clickRegister();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> toolsPage.isToolInTable("Serra Elétrica"));

        driver.get("http://localhost:5173/customers");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                !d.findElements(By.xpath("//input[@placeholder='Nome do cliente']")).isEmpty());
        driver.findElement(By.xpath("//input[@placeholder='Nome do cliente']")).sendKeys("Cliente Cancel");
        driver.findElement(By.xpath("//button[text()='Cadastrar']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                !d.findElements(By.xpath("//td[text()='Cliente Cancel']")).isEmpty());

        driver.get("http://localhost:5173/rental");
        RentalPage rentalPage = new RentalPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                !d.findElements(By.className("tool-row")).isEmpty());
        rentalPage.clickFirstToolRow();
        rentalPage.searchCustomer("Cancel");
        rentalPage.selectCustomerFromDropdown("Cliente Cancel");
        rentalPage.selectGuarantee("Dinheiro");
        rentalPage.clickConfirm();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isSuccessMessageVisible());

        rentalPage.clickActiveRentalsTab();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isCancelButtonVisible());
        assertThat(rentalPage.isCancelButtonVisible()).isTrue();

        int countBefore = rentalPage.getActiveRentalCount();
        rentalPage.clickCancelButton();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                rentalPage.getActiveRentalCount() < countBefore);
        assertThat(rentalPage.getActiveRentalCount()).isLessThan(countBefore);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Active rentals tab should show the active rentals content")
    public void shouldShowActiveRentalsTabContentWhenTheTabIsSelected() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        HeaderPage headerPage = new HeaderPage(driver);
        headerPage.clickRental();

        RentalPage rentalPage = new RentalPage(driver);
        rentalPage.clickActiveRentalsTab();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> rentalPage.isRentalTableVisible());
        assertThat(rentalPage.isRentalTableVisible()).isTrue();

        if (rentalPage.isFinalizeButtonVisible()) {
            assertThat(rentalPage.isFinalizeButtonVisible()).isTrue();
            assertThat(rentalPage.isCancelButtonVisible()).isTrue();
        } else {
            assertThat(rentalPage.isEmptyStateMessageVisible()).isTrue();
            assertThat(rentalPage.getEmptyStateMessage()).isEqualTo("Nenhuma locação ativa.");
        }
    }
}
