package br.ifsp.demo.ui.pages;

import br.ifsp.demo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PublicRoutePage extends BasePage {

    public PublicRoutePage(WebDriver driver) {
        super(driver);
    }

    public boolean isRegisterHeaderVisible() {
        return !driver.findElements(By.xpath("//h1[text()='Cadastro']")).isEmpty();
    }
}
