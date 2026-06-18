package com.renan.aws.sns.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

@Service
public class SnsService {

    // ARN é o identificador unico do topico no SNS
    // formato: arn:aws:sns:<regiao>:<id-da-conta>:<nome-do-topico>
    // esse valor é gerado quando o topico é criado no console AWS
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-2:213180001857:aulaTopicosSns";

    private final SnsClient snsClient;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String publicar(String mensagem) {
        // PublishRequest monta o pacote que vai ser enviado para o topico
        // topicArn: qual topico vai receber
        // subject: assunto (aparece no email se tiver assinante de email)
        // message: o conteudo da mensagem
        PublishRequest request = PublishRequest.builder()
                .topicArn(TOPIC_ARN)
                .subject("Notificacao AWS Spring Lab")
                .message(mensagem)
                .build();

        // snsClient.publish() envia a mensagem para o topico
        // o SNS distribui para todos os assinantes registrados automaticamente
        // assinantes podem ser: email, http, sqs, lambda, sms
        PublishResponse response = snsClient.publish(request);

        System.out.println("Mensagem publicada no topico. MessageId: " + response.messageId());

        // messageId é gerado pelo SNS para rastrear a mensagem
        return "Mensagem publicada. ID: " + response.messageId();
    }

    public String inscreverEmail(String email) {
        // SubscribeRequest registra um assinante no topico
        // protocol "email": o SNS vai mandar um email para o endereco informado
        // antes de receber mensagens, a pessoa precisa clicar em "Confirm subscription"
        // o SNS nao entrega mensagens para emails nao confirmados
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(TOPIC_ARN)
                .protocol("email")
                .endpoint(email)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);

        System.out.println("Inscricao de email criada: " + email);

        return "Inscricao criada. ARN: " + response.subscriptionArn()
                + " (verifique seu email e clique em Confirm subscription)";
    }

    public String inscreverHttp(String endpointUrl) {
        // protocol "http" ou "https": o SNS vai fazer um POST no endpoint informado
        // quando uma mensagem for publicada no topico
        // o SNS primeiro manda uma SubscriptionConfirmation — o endpoint precisa confirmar
        // so depois disso comeca a receber as Notifications de verdade
        String protocolo = endpointUrl.startsWith("https") ? "https" : "http";

        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(TOPIC_ARN)
                .protocol(protocolo)
                .endpoint(endpointUrl)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);

        System.out.println("Inscricao HTTP criada para: " + endpointUrl);

        return "Inscricao criada. ARN: " + response.subscriptionArn();
    }
}
