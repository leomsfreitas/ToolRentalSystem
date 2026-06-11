package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class PrivateRouteTest extends BaseUiTest {

    @Test
    @Tag("UiTest")
    @DisplayName("Unauthenticated users are blocked from private routes")
    public void unauthenticatedUsersAreBlockedFromPrivateRoute() {
        driver.get("http://localhost:5173/");
        ((JavascriptExecutor) driver).executeScript("window.localStorage.removeItem('token');");
        
        driver.get("http://localhost:5173/home");
        
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !d.getCurrentUrl().contains("/home"));
        
        assertThat(driver.getCurrentUrl()).isEqualTo("http://localhost:5173/");
        
        assertThat(driver.findElements(By.xpath("//h1[text()='Ferramentas disponíveis']"))).isEmpty();
    }
}
