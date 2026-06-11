package br.ifsp.demo.ui.helpers;

import com.github.javafaker.Faker;

public class UiTestDataFactory {

    private static final Faker faker = new Faker();

    public static String createName() {
        return faker.name().fullName();
    }

    public static String createLastName() {
        return faker.name().lastName();
    }

    public static String createEmail() {
        return faker.internet().emailAddress();
    }

    public static String createPassword() {
        return "Test@1234";
    }
}
