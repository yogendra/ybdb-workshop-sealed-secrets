package io.dsql.sealedsecret;

import java.util.function.Function;

import javax.sql.DataSource;

import com.yugabyte.ds.PGSimpleDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.dsql.sealedsecret.SecretManagerProperties.Type;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(SecretManagerProperties.class)
public class SealedSecretApplication {

	public static void main(String[] args) {
		SpringApplication.run(SealedSecretApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("spring.datasource.hikari")
	public HikariConfig config() {
		return new HikariConfig();
	}

	@Bean
	public DataSource yugabteDBDatasource(DataSourceProperties properties,
			SecretManagerProperties secretManagerProperties) {
		Function<Type, String> secretConfig = secretManagerProperties.getSecretConfig();
		PGSimpleDataSource wrappedDataSource = properties.initializeDataSourceBuilder().type(PGSimpleDataSource.class)
				.build();
		wrappedDataSource.setSslRootCert(secretConfig.apply(Type.ROOT_CERT));
		if (secretManagerProperties.isMutualTLS()) {
			wrappedDataSource.setSslKey(secretConfig.apply(Type.CLIENT_KEY));
			wrappedDataSource.setSslCert(secretConfig.apply(Type.CLIENT_CERT));
		}
		HikariConfig config = config();
		config.setDataSource(wrappedDataSource);
		return new HikariDataSource(config);
	}

}
