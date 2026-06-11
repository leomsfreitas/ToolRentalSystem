package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.MaintenancePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class MaintenanceTest extends BaseUiTest {

    @Test
    @Tag("UiTest")
    @DisplayName("Maintenance page should render the tabs and lists")
    public void shouldRenderMaintenancePageWithTabsAndLists() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/maintenance");
        MaintenancePage page = new MaintenancePage(driver);

        assertThat(page.isHeadingVisible()).isTrue();
        assertThat(page.isAvailableTabVisible()).isTrue();
        assertThat(page.isActiveMaintenanceTabVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Sending a tool to maintenance should update the lists")
    public void shouldSendAAvailableToolToMaintenance() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        createTool("Furadeira", 10.0, 50.0, 150.0);

        driver.get("http://localhost:5173/maintenance");
        MaintenancePage page = new MaintenancePage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.isSendToMaintenanceButtonVisible());

        int countBefore = page.getAvailableToolCount();
        page.clickSendToMaintenanceButton();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                page.getAvailableToolCount() < countBefore);
        assertThat(page.getAvailableToolCount()).isLessThan(countBefore);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Returning a tool from maintenance should update the lists")
    public void shouldReturnAToolFromMaintenance() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        String toolId = createToolAndGetId("Parafusadeira", 15.0, 60.0, 180.0);
        sendToolToMaintenanceViaApi(toolId);

        driver.get("http://localhost:5173/maintenance");
        MaintenancePage page = new MaintenancePage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isHeadingVisible());

        page.clickActiveMaintenanceTab();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.isReturnFromMaintenanceButtonVisible());

        int countBefore = page.getMaintenanceToolCount();
        page.clickReturnFromMaintenanceButton();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                page.getMaintenanceToolCount() < countBefore);
        assertThat(page.getMaintenanceToolCount()).isLessThan(countBefore);
    }

    @Test
    @Tag("UiTest")
    @DisplayName("The toolid query parameter should highlight the selected tool")
    public void shouldHighlightTheToolProvidedInTheToolIdQueryParameter() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        String toolId = createToolAndGetId("Lixadeira", 12.0, 55.0, 160.0);

        driver.get("http://localhost:5173/maintenance?toolId=" + toolId);
        MaintenancePage page = new MaintenancePage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> page.isSendToMaintenanceButtonVisible());

        assertThat(page.isSelectedRowVisible()).isTrue();
        assertThat(page.getSelectedRowCount()).isEqualTo(1);
    }
}
