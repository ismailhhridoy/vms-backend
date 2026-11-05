package com.starkgroup.vms.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

@Slf4j
@Configuration
public class DatabaseConfig {

    public static class NormalizedDbSettings {
        public final String jdbcUrl;
        public final String username;
        public final String password;

        public NormalizedDbSettings(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }
    }

    public static NormalizedDbSettings normalize(String rawUrl, String username, String password) {
        if (!StringUtils.hasText(rawUrl)) {
            return new NormalizedDbSettings(rawUrl, username, password);
        }

        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            try {
                String normalizedSchemeUrl = rawUrl.replaceFirst("^postgres://", "postgresql://");
                URI uri = URI.create(normalizedSchemeUrl);

                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String host = uri.getHost();
                String path = uri.getPath();

                String cleanJdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    if (!StringUtils.hasText(username)) {
                        username = userInfo[0];
                    }
                    if (userInfo.length > 1 && !StringUtils.hasText(password)) {
                        password = userInfo[1];
                    }
                }

                log.info("Transformed cloud PostgreSQL URL into JDBC format for host: {}, database: {}", host, path);
                rawUrl = cleanJdbcUrl;
            } catch (Exception e) {
                log.warn("Could not parse database URL as URI, prepending jdbc: prefix directly: {}", e.getMessage());
                if (!rawUrl.startsWith("jdbc:")) {
                    rawUrl = "jdbc:" + rawUrl;
                }
            }
        } else if (!rawUrl.startsWith("jdbc:")) {
            rawUrl = "jdbc:" + rawUrl;
        }

        return new NormalizedDbSettings(rawUrl, username, password);
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        NormalizedDbSettings settings = normalize(
                properties.getUrl(),
                properties.getUsername(),
                properties.getPassword()
        );

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(settings.jdbcUrl);
        if (StringUtils.hasText(settings.username)) {
            config.setUsername(settings.username);
        }
        if (StringUtils.hasText(settings.password)) {
            config.setPassword(settings.password);
        }
        if (StringUtils.hasText(properties.getDriverClassName())) {
            config.setDriverClassName(properties.getDriverClassName());
        }

        return new HikariDataSource(config);
    }
}
