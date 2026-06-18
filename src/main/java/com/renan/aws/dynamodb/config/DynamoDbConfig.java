package com.renan.aws.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                // DefaultCredentialsProvider pega as credenciais automaticamente
                // quando roda na EC2, ele usa a Role IAM da instancia (sem precisar colocar chave)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
