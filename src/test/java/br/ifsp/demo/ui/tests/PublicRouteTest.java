package br.ifsp.demo.ui.tests;

import br.ifsp.demo.ui.base.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicRouteTest extends BaseUiTest {

    @Test
    @Tag("UiTest")
    @DisplayName("Authenticated users are redirected away from public routes")
    public void authenticatedUsersAreRedirectedAwayFromPublicRoute() {
        driver.get("http://localhost:5173/");
        
        ((JavascriptExecutor) driver).executeScript("window.localStorage.setItem('token', 'fake-token');");
        
        driver.get("http://localhost:5173/register");
        
        assertThat(driver.getCurrentUrl()).contains("/home");
        
        assertThat(driver.findElements(By.xpath("//h1[text()='Cadastro']"))).isEmpty();
    }
}
