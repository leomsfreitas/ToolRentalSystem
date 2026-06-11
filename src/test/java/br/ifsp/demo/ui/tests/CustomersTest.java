package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.CustomersPage;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class CustomersTest extends BaseUiTest {

    private static final Faker faker = new Faker();

    @Test
    @Tag("UiTest")
    @DisplayName("Customers page should render the form and the customer table")
    public void shouldRenderCustomerFormAndCustomerTable() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/customers");
        CustomersPage page = new CustomersPage(driver);

        assertThat(page.isHeadingVisible()).isTrue();
        assertThat(page.isNameInputVisible()).isTrue();
        assertThat(page.isSubmitButtonVisible()).isTrue();
        assertThat(page.isNameHeaderVisible()).isTrue();
        assertThat(page.isIdHeaderVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Valid customer creation should add the customer to the list")
    public void shouldCreateACustomerAndShowItInTheList() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/customers");
        CustomersPage page = new CustomersPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isHeadingVisible());

        String customerName = faker.name().fullName();
        page.fillName(customerName);
        page.clickCadastrar();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isCustomerInTable(customerName));
        assertThat(page.isCustomerInTable(customerName)).isTrue();
        assertThat(page.isSubmitErrorVisible()).isFalse();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Invalid customer creation should show an error message")
    public void shouldShowErrorWhenCustomerCreationFails() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/customers");
        CustomersPage page = new CustomersPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isHeadingVisible());

        String customerName = faker.name().fullName();
        page.fillName(customerName);

        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', 'invalid-token');");

        page.clickCadastrar();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isSubmitErrorVisible());
        assertThat(page.isSubmitErrorVisible()).isTrue();
        assertThat(page.isCustomerInTable(customerName)).isFalse();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Customers loading failure should show an error message")
    public void shouldShowErrorWhenCustomersCannotBeLoaded() {
        driver.get("http://localhost:5173/");
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', 'invalid-token');");

        driver.get("http://localhost:5173/customers");
        CustomersPage page = new CustomersPage(driver);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isLoadErrorVisible());
        assertThat(page.isLoadErrorVisible()).isTrue();
        assertThat(page.hasNoCustomerRows()).isTrue();
    }
}
