package br.ifsp.demo.integration.util;

import com.github.javafaker.Faker;
import java.util.Locale;

public class EntityBuilder {

    private static final Faker faker = Faker.instance();

    public static String randomFirstName() {
        return faker.name().firstName().replaceAll("[^a-zA-Z]", "");
    }

    public static String randomLastName() {
        return faker.name().lastName().replaceAll("[^a-zA-Z]", "");
    }

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomCustomerName() {
        return faker.name().fullName().replaceAll("[^a-zA-Z ]", "").trim();
    }

    public static String randomToolName() {
        return faker.commerce().productName().replaceAll("[^a-zA-Z0-9 ]", "").trim();
    }

    public static double randomDailyRate() {
        return faker.number().randomDouble(2, 5, 50);
    }

    public static double randomWeeklyRate(double dailyRate) {
        return Math.round(dailyRate * 0.8 * 100.0) / 100.0;
    }

    public static double randomMonthlyRate(double dailyRate) {
        return Math.round(dailyRate * 0.6 * 100.0) / 100.0;
    }

    public static String registerUserBody() {
        return String.format(
                "{\"name\":\"%s\",\"lastname\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                randomFirstName(), randomLastName(), randomEmail(), "Test@1234");
    }

    public static String registerUserBody(String email, String password) {
        return String.format(
                "{\"name\":\"%s\",\"lastname\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                randomFirstName(), randomLastName(), email, password);
    }

    public static String createCustomerBody() {
        return String.format("{\"name\":\"%s\"}", randomCustomerName());
    }

    public static String createCustomerBody(String name) {
        return String.format("{\"name\":\"%s\"}", name);
    }

    public static String createToolBody() {
        double daily = randomDailyRate();
        return String.format(Locale.US,
                "{\"name\":\"%s\",\"dailyRate\":%.2f,\"weeklyDailyRate\":%.2f,\"monthlyDailyRate\":%.2f}",
                randomToolName(), daily, randomWeeklyRate(daily), randomMonthlyRate(daily));
    }

    public static String createToolBody(String name) {
        double daily = randomDailyRate();
        return String.format(Locale.US,
                "{\"name\":\"%s\",\"dailyRate\":%.2f,\"weeklyDailyRate\":%.2f,\"monthlyDailyRate\":%.2f}",
                name, daily, randomWeeklyRate(daily), randomMonthlyRate(daily));
    }
}
