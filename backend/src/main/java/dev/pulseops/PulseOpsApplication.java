package dev.pulseops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class PulseOpsApplication {
    public static void main(String[] args) {
        configureRenderDatabaseUrl();
        configureRenderRedisUrl();
        SpringApplication.run(PulseOpsApplication.class, args);
    }

    private static void configureRenderDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank() || !databaseUrl.startsWith("postgres")) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        String[] userInfo = uri.getUserInfo() == null ? new String[0] : uri.getUserInfo().split(":", 2);
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();

        if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
            jdbcUrl += "?" + uri.getQuery();
        }

        System.setProperty("spring.datasource.url", jdbcUrl);
        if (userInfo.length > 0) {
            System.setProperty("spring.datasource.username", decode(userInfo[0]));
        }
        if (userInfo.length > 1) {
            System.setProperty("spring.datasource.password", decode(userInfo[1]));
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static void configureRenderRedisUrl() {
        String redisUrl = System.getenv("REDIS_URL");
        if (redisUrl == null || redisUrl.isBlank() || !redisUrl.startsWith("redis")) {
            return;
        }

        URI uri = URI.create(redisUrl);
        if (uri.getHost() != null && uri.getHost().startsWith("red-") && !uri.getHost().contains(".")) {
            System.setProperty("spring.data.redis.url", redisUrl.replaceFirst("^rediss://", "redis://"));
            System.setProperty("spring.data.redis.ssl.enabled", "false");
        }
    }
}
