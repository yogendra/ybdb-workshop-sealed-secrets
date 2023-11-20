# ybdb-sealed-secrets

[![SpringBoot CI with Maven](../../actions/workflows/build.yml/badge.svg?branch=main)](../../actions/workflows/build.yml/badge.svg)

[Spring Boot](https://spring.io/projects/spring-boot) makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

This describes securing your Spring Boot application with YugabyteDB over TLS using all of the well-known native cloud secret management services

## Getting Started with Gitpod:
[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/yogendra/ybdb-workshop-sealed-secrets)


## AWS Secrets Manager:
Spring Cloud for Amazon Web Services eases integration with hosted Amazon Web Services. The Maven starter dependency to activate this convention is as follows:
```
<dependency>
  <groupId>io.awspring.cloud</groupId>
  <artifactId>spring-cloud-aws-starter-secrets-manager</artifactId>
</dependency>
```

Refer to the `<id>sm-aws</id>` section in the Maven build file (pom.xml) for the complete definition. The dependency mentioned above simplifies the secrets-manager integration by automatically configuring the relevant beans, avoiding a lot of boilerplate code.

The Spring profile configuration is available in [application-aws.yml](src/main/resources/application-aws.yml).
```
spring:
  config:
    import: aws-secretsmanager:ybdb-vault
  cloud:
    aws:
      credentials:
        profile:
          name: ${AWS_PROFILE}
      secretsmanager:
        region: ap-south-1
      region:
        profile:
          name: ${AWS_PROFILE}
  datasource:
    username: ${ybdb-username}
    password: ${ybdb-password}
ybdb:
  rootCert: ${ybdb-root-cert}
  clientKey: ${ybdb-client-key}
  clientCert: ${ybdb-client-cert}
```
Create the following in the secrets-manager service:
* Create a secret-id named `ybdb-vault`
* Under `ybdb-vault` secret, create the following key/value pairs
  - key: ybdb-username, value: <value>
  - key: ybdb-password, value: <value>
  - key: ybdb-root-cert, value: <value> in base64 encoded format.

During boot time, the aws starter dependency will import the keys from the secret-id `ybdb-vault` and populate the relevant property placeholders in the above configuration file.

### Build the app:
To enable the aws-secrets-manager build profile, set the environment variable `env.scp=aws`.

`mvn -Denv.scp=aws clean package`

### Run the app:
To activate the aws-specific binding profile, specify the spring profile VM argument.

`java -jar -Dspring.profiles.active=aws ./target/sealedsecret-1.0.jar`


## GCP Secret Manager:
Spring Cloud for GCP eases integration with managed GCP Services. The Maven starter dependency to enable convention over configuration is as follows:
```
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>spring-cloud-gcp-starter-secretmanager</artifactId>
</dependency>
```

Refer to the `<id>sm-gcp</id>` section in the Maven build file (pom.xml) for the complete definition. The dependency mentioned above simplifies the secretmanager integration by automatically configuring the relevant beans, avoiding a lot of boilerplate code.

The Spring profile configuration is available in [application-aws.yml](src/main/resources/application-gcp.yml).
```
spring:
  config:
    import: sm://
  cloud:
    gcp:
      project-id: ${GOOGLE_CLOUD_PROJECT}
      credentials:
        location: ${GOOGLE_APPLICATION_CREDENTIALS}
      secretmanager:
        enabled: true
  datasource:
    username: ${sm://ybdb-username}
    password: ${sm://ybdb-password}
ybdb:
  rootCert: ${sm://ybdb-root-cert}
  clientKey: ${sm://ybdb-client-key}
  clientCert: ${sm://ybdb-client-cert}
```

Create the following in the secretmanager service:
* Create the following key/value pairs
  - key: ybdb-username, value: <value>
  - key: ybdb-password, value: <value>
  - key: ybdb-root-cert, value: <value> in base64 encoded format.

During boot time, the gcp starter dependency will import the keys and populate the relevant property placeholders in the above configuration file.

### Build the app:
To enable the gcp-secretmanager build profile, set the environment variable `env.scp=gcp`.

`mvn -Denv.scp=gcp clean package`

### Run the app:
To activate the gcp-specific binding profile, specify the spring profile VM argument.

`java -jar -Dspring.profiles.active=gcp ./target/sealedsecret-1.0.jar`

## Azure Key Vault:
Spring Cloud for Azure eases integration with hosted Azure Services. The Maven starter dependency to activate this convention is as follows:

```
<dependency>
  <groupId>com.azure.spring</groupId>
  <artifactId>spring-cloud-azure-starter-keyvault</artifactId>
</dependency>
```

Refer to the `<id>sm-azure</id>` section in the Maven build file (pom.xml) for the complete definition. The dependency mentioned above simplifies the keyvault integration by automatically configuring the relevant beans, avoiding a lot of boilerplate code.

The Spring profile configuration is available in [application-aws.yml](src/main/resources/application-azure.yml).
```
spring:
  cloud:
    azure:
      key-vault:
        secret:
          property-sources[0]:
            name: ${AZURE_KEY_VAULT_NAME}
            endpoint: ${AZURE_KEY_VAULT_ENDPOINT}
            secret-keys: # pull only these keys
              - ybdb-username
              - ybdb-password
              - ybdb-root-cert
              - ybdb-client-key
              - ybdb-client-cert
  datasource:
    username: ${ybdb-username}
    password: ${ybdb-password}
ybdb:
  rootCert: ${ybdb-root-cert}
  clientKey: ${ybdb-client-key}
  clientCert: ${ybdb-client-cert}
```

Create the following in the keyvault service:
* Create the following key/value pairs
  - key: ybdb-username, value: <value>
  - key: ybdb-password, value: <value>
  - key: ybdb-root-cert, value: <value> in base64 encoded format.

During boot time, the azure starter dependency will import the mentioned keys from the keyvault endpoint service and populate the relevant property placeholders in the above configuration file.

### Build the app:
To enable the azure-keyvault build profile, set the environment variable `env.scp=azure`.

`mvn -Denv.scp=azure clean package`

### Run the app:
To activate the azure-specific binding profile, specify the spring profile VM argument.

`java -jar -Dspring.profiles.active=azure ./target/sealedsecret-1.0.jar`

## Hashicorp Vault:
Spring Cloud Vault eases integration with the hosted/managed Vault service. The Maven starter dependency to activate this convention is as follows:

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

Refer to the `<id>sm-vault</id>` section in the Maven build file (pom.xml) for the complete definition. The dependency mentioned above simplifies the vault integration by automatically configuring the relevant beans, avoiding a lot of boilerplate code.

The Spring profile configuration is available in [application-vault.yml](src/main/resources/application-vault.yml).

```
spring:
  config:
    import: vault://
  cloud:
    vault:
      token: #{systemProperties['VAULT_TOKEN']}
      kv:
        enabled: true
      scheme: http
  datasource:
    username: ${ybdb-username}
    password: ${ybdb-password}
ybdb:
  rootCert: ${ybdb-root-cert}
  clientKey: ${ybdb-client-key}
  clientCert: ${ybdb-client-cert}
```

Create the following in the vault service:
* Create a secret path for the app named ybdb-vault (this matches the spring.application.name property)
* Under ybdb-vault, create the following key/value pairs
  - key: ybdb-username, value: <value>
  - key: ybdb-password, value: <value>
  - key: ybdb-root-cert, value: <value> in base64 encoded format.

During boot time, the vault config starter dependency will import the keys from the secret-path `ybdb-vault` and populate the relevant property placeholders in the above configuration file.

### Build the app:
To enable the vault build profile, set the environment variable `env.scp=vault`.

`mvn -Denv.scp=vault clean package`

### Run the app:
To activate the vault-specific binding profile, specify the spring profile VM argument.

`java -jar -Dspring.profiles.active=vault ./target/sealedsecret-1.0.jar`
