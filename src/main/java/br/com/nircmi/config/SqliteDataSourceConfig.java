package br.com.nircmi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SqliteDataSourceConfig {

    @Value("${nircmi.datasource.connect-string:nir_cmi.db}")
    private String connectString;

    @Value("${nircmi.datasource.pool-max:1}")
    private int poolMax;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // SQLite connection URL syntax: jdbc:sqlite:<file-path>
        config.setJdbcUrl("jdbc:sqlite:" + connectString);
        config.setDriverClassName("org.sqlite.JDBC");
        
        // SQLite uses file-level locking; max pool size of 1 is recommended for single-writer safety
        config.setMaximumPoolSize(poolMax);
        config.setPoolName("NirCmiSqlitePool");
        
        return new HikariDataSource(config);
    }
}
