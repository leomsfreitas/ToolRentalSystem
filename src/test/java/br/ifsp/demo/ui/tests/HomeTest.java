package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.HomePage;
import br.ifsp.demo.ui.pages.MaintenancePage;
import br.ifsp.demo.ui.pages.RentalPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class HomeTest extends BaseUiTest {
    private HomePage homePage;

    @BeforeEach
    public void setup() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        createTool("Furadeira", 10.0, 50.0, 150.0);
        driver.navigate().refresh();

        homePage = new HomePage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> homePage.isEndDateInputVisible());
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> homePage.isToolTableVisible());
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Home page should render available tools and simulation controls")
    public void shouldRenderHomePageWithAvailableToolsSection() {
        assertThat(homePage.isHeadingVisible()).isTrue();
        assertThat(homePage.isEndDateInputVisible()).isTrue();
        assertThat(homePage.isSimulateButtonVisible()).isTrue();
        assertThat(homePage.isGoToRentalsButtonVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Selecting a tool should toggle its selected state")
    public void shouldToggleToolSelectionInTheTable() {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> homePage.isToolTableVisible());

        homePage.clickFirstToolRow();
        assertThat(homePage.isFirstToolRowSelected()).isTrue();

        homePage.clickFirstToolRow();
        assertThat(homePage.isFirstToolRowSelected()).isFalse();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Simulation should require at least one selected tool")
    public void shouldShowValidationWhenSimulatingWithoutSelectingAnyTool() {
        homePage.setEndDate("31122026");
        homePage.clickSimulate();

        assertThat(homePage.getErrorMessage()).isEqualTo("Selecione ao menos uma ferramenta.");
        assertThat(homePage.isSimulationValueVisible()).isFalse();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Simulation should require a return date")
    public void shouldShowValidationWhenSimulatingWithoutReturnDate() {
        homePage.clickFirstToolRow();
        homePage.clickSimulate();

        assertThat(homePage.getErrorMessage()).isEqualTo("Informe a data de devolução.");
        assertThat(homePage.isSimulationValueVisible()).isFalse();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Successful simulation should show the estimated rental value")
    public void shouldDisplayEstimatedValueAfterSuccessfulSimulation() {
        homePage.clickFirstToolRow();
        homePage.setEndDate("31122026");

        homePage.clickSimulate();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> homePage.isSimulationValueVisible());

        assertThat(homePage.getSimulationResultText()).contains("Valor estimado: R$");
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Home should navigate to the rentals page")
    public void shouldNavigateToRentalPageWhenGoToRentalsButtonIsClicked() {
        homePage.clickGoToRentals();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().endsWith("/rental"));

        assertThat(driver.getCurrentUrl()).endsWith("/rental");
        RentalPage rentalPage = new RentalPage(driver);
        assertThat(rentalPage.isHeadingVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Maintenance action should navigate to the maintenance page with the selected tool")
    public void shouldNavigateToMaintenancePageWhenMaintenanceActionIsClicked() {
        homePage.clickMaintenanceOfFirstTool();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> d.getCurrentUrl().contains("/maintenance"));

        assertThat(driver.getCurrentUrl()).contains("/maintenance");
        assertThat(driver.getCurrentUrl()).contains("toolId=");
        
        MaintenancePage maintenancePage = new MaintenancePage(driver);
        assertThat(maintenancePage.isHeadingVisible()).isTrue();
    }
}
