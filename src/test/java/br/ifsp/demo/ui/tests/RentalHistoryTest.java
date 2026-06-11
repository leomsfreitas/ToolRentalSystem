package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import br.ifsp.demo.ui.helpers.UiTestDataFactory;
import br.ifsp.demo.ui.pages.HeaderPage;
import br.ifsp.demo.ui.pages.RentalHistoryPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class RentalHistoryTest extends BaseUiTest {

    @Test
    @Tag("UiTest")
    @DisplayName("Rental history page should render the tabs and main heading")
    public void shouldRenderRentalHistoryTabsAndMainHeading() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/rental/history");
        RentalHistoryPage page = new RentalHistoryPage(driver);

        assertThat(page.isHeadingVisible()).isTrue();
        assertThat(page.isRentalsTabVisible()).isTrue();
        assertThat(page.isMaintenancesTabVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Switching history tabs should update the visible content")
    public void shouldSwitchBetweenRentalsAndMaintenanceTabs() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        driver.get("http://localhost:5173/rental/history");
        RentalHistoryPage page = new RentalHistoryPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isHeadingVisible());

        page.clickMaintenancesTab();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isTableVisible());
        assertThat(page.isTableVisible()).isTrue();

        page.clickRentalsTab();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isTableVisible());
        assertThat(page.isTableVisible()).isTrue();
    }

    @Test
    @Tag("UiTest")
    @DisplayName("Empty history tabs should show their empty states")
    public void shouldShowEmptyStateWhenThereAreNoFinishedItems() {
        String email = UiTestDataFactory.createEmail();
        String password = UiTestDataFactory.createPassword();
        registerUser("Admin", "User", email, password);
        login(email, password);

        ((JavascriptExecutor) driver).executeScript(
            "var orig = window.fetch;" +
            "window.fetch = function(url, opts) {" +
            "  var method = (opts && opts.method) ? opts.method.toUpperCase() : 'GET';" +
            "  if (method === 'GET' && (url.includes('/rentals') || url.includes('/maintenance'))) {" +
            "    return Promise.resolve({" +
            "      ok: true," +
            "      headers: { get: function() { return 'application/json'; } }," +
            "      json: function() { return Promise.resolve([]); }" +
            "    });" +
            "  }" +
            "  return orig.apply(this, arguments);" +
            "};"
        );

        HeaderPage headerPage = new HeaderPage(driver);
        headerPage.clickHistory();

        RentalHistoryPage page = new RentalHistoryPage(driver);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isHeadingVisible());

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isRentalsEmptyStateVisible());
        assertThat(page.isRentalsEmptyStateVisible()).isTrue();

        page.clickMaintenancesTab();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> page.isMaintenancesEmptyStateVisible());
        assertThat(page.isMaintenancesEmptyStateVisible()).isTrue();
    }
}
