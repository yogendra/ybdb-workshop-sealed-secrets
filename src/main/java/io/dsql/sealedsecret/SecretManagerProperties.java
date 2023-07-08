package io.dsql.sealedsecret;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ybdb")
@Getter
@Setter
@ToString
public class SecretManagerProperties {

	Function<Type, String> secretConfig = SecretManagerProperties.this::getFilePath;

	enum Type {

		// @formatter:off
		ROOT_CERT("root_", ".crt"),
		CLIENT_KEY("client_", ".p8"),
		CLIENT_CERT("client_", ".crt");
		// @formatter:on
		private final String prefix;

		private final String suffix;

		Type(String prefix, String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
		}

	}

	private String rootCert;

	private String clientKey;

	private String clientCert;

	private boolean mutualTLS;

	private String getTypeConfig(Type type) {
		return switch (type) {
		case ROOT_CERT -> rootCert;
		case CLIENT_KEY -> clientKey;
		case CLIENT_CERT -> clientCert;
		};
	}

	@SneakyThrows
	private String getFilePath(Type type) {
		var file = File.createTempFile(type.prefix, type.suffix);
		Files.write(Paths.get(file.getPath()), Base64.getDecoder().decode(getTypeConfig(type)));
		Runtime.getRuntime().addShutdownHook(new Thread(file::deleteOnExit));
		return file.getPath();
	}

}
