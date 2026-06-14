package io.github.atsin.dashr.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseKeepAlive {
    private final JdbcTemplate jdbcTemplate;

    // Ping every 55 minutes — keeps Neon from sleeping during active use
    // but saves credits when app itself is idle
    @Scheduled(fixedRate = 3300000)
    public void keepAlive() {
        try {
            jdbcTemplate.execute("SELECT 1");
            log.debug("Neon database keep-alive ping successful");
        } catch (Exception e) {
            log.debug("Neon database keep-alive ping failed (waking up?): {}", e.getMessage());
        }
    }
}
