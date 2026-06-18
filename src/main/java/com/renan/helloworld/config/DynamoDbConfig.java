package com.renan.helloworld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

// @Configuration indica que esta classe declara Beans gerenciados pelo Spring
@Configuration
public class DynamoDbConfig {

    // @Bean registra o DynamoDbClient no contexto do Spring — qualquer classe
    // que precisar pode receber via injeção de dependência no construtor
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                // Região onde a tabela DynamoDB foi criada no Console
                .region(Region.US_EAST_2)
                // DefaultCredentialsProvider busca credenciais automaticamente:
                // 1. Variáveis de ambiente (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY)
                // 2. ~/.aws/credentials (perfil local)
                // 3. Role IAM da EC2 — é o que usamos em produção (sem credencial hardcoded)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
