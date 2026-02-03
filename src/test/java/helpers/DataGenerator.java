package helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.javafaker.Faker;

import net.minidev.json.JSONObject;

public class DataGenerator {
    
    public static String getRandomEmail(){
        Faker faker = new Faker();
        String email = faker.name().firstName().toLowerCase() + faker.random().nextInt(0, 100) + "@test.com";
        return email;
    }

   public static String getRandomUsername() {
        Faker faker = new Faker();
        String username = faker.name().username();

        if (username == null) {
            return null;
        }

        return username.length() <= 20
                ? username
                : username.substring(0, 20);
    }

    public static JSONObject getRandomarticleValues() {
        Faker faker = new Faker();
        String title = faker.gameOfThrones().character();
        String description = faker.gameOfThrones().city();
        String body = faker.gameOfThrones().quote();
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);
        json.put("body", body);
        return json;
    }

    public static String getTimestamp() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
    
}
