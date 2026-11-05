package com.starkgroup.vms.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConfigTest {

    @Test
    @DisplayName("Should transform Render cloud postgresql URL into standard JDBC URL and extract credentials")
    void shouldTransformRenderPostgresqlUrl() {
        String raw = "postgresql://vms_user:secretPass123@dpg-dbm77i9srm7s73f9ihkg-a/vms_db_jx7t";
        DatabaseConfig.NormalizedDbSettings settings = DatabaseConfig.normalize(raw, null, null);

        assertThat(settings.jdbcUrl).isEqualTo("jdbc:postgresql://dpg-dbm77i9srm7s73f9ihkg-a:5432/vms_db_jx7t");
        assertThat(settings.username).isEqualTo("vms_user");
        assertThat(settings.password).isEqualTo("secretPass123");
    }

    @Test
    @DisplayName("Should transform legacy postgres:// URL into standard JDBC URL")
    void shouldTransformLegacyPostgresUrl() {
        String raw = "postgres://admin:pwd@myhost:5433/fleet_db";
        DatabaseConfig.NormalizedDbSettings settings = DatabaseConfig.normalize(raw, null, null);

        assertThat(settings.jdbcUrl).isEqualTo("jdbc:postgresql://myhost:5433/fleet_db");
        assertThat(settings.username).isEqualTo("admin");
        assertThat(settings.password).isEqualTo("pwd");
    }

    @Test
    @DisplayName("Should preserve standard jdbc: URL as is")
    void shouldPreserveStandardJdbcUrl() {
        String raw = "jdbc:h2:mem:testdb";
        DatabaseConfig.NormalizedDbSettings settings = DatabaseConfig.normalize(raw, "sa", "");

        assertThat(settings.jdbcUrl).isEqualTo("jdbc:h2:mem:testdb");
        assertThat(settings.username).isEqualTo("sa");
        assertThat(settings.password).isEqualTo("");
    }
}
