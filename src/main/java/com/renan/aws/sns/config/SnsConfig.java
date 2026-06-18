package com.renan.aws.sns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SnsConfig {

    @Bean
    public SnsClient snsClient() {
        // SnsClient é o objeto que a gente usa para falar com o SNS da AWS
        // DefaultCredentialsProvider pega as credenciais automaticamente:
        //   - rodando local: usa o ~/.aws/credentials (aws configure)
        //   - rodando na EC2: usa a Role cursoAssurance anexada na instancia
        // assim o codigo nao precisa de senha hardcoded em lugar nenhum
        return SnsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
