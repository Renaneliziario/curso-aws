package com.renan.aws.sns.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ConfirmSubscriptionRequest;

@Service
public class SnsReceiverService {

    private final SnsClient snsClient;

    public SnsReceiverService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String confirmarInscricao(JsonNode payload) {
        // quando registramos um endpoint HTTP no topico, o SNS nao confia de imediato
        // ele manda primeiro uma mensagem do tipo SubscriptionConfirmation com um Token
        // precisamos devolver esse Token para provar que somos donos do endpoint
        // so depois disso o SNS comeca a entregar as mensagens reais (Notification)
        String topicArn = payload.get("TopicArn").asText();
        String token = payload.get("Token").asText();

        // ConfirmSubscriptionRequest usa o topicArn + token para confirmar via SDK
        // o proprio SDK da AWS faz a chamada de confirmacao para o SNS
        snsClient.confirmSubscription(ConfirmSubscriptionRequest.builder()
                .topicArn(topicArn)
                .token(token)
                .build());

        System.out.println("Inscricao SNS confirmada para topico: " + topicArn);

        return "Subscription confirmed";
    }

    public String processarNotificacao(JsonNode payload) {
        // aqui chegam as mensagens reais publicadas no topico
        // o campo "Message" contem o conteudo que foi publicado
        // em producao aqui entraria a logica de negocio:
        //   - salvar no banco de dados
        //   - chamar outro servico
        //   - atualizar cache
        //   - disparar outra fila SQS
        String mensagem = payload.get("Message").asText();

        System.out.println("Mensagem recebida do SNS: " + mensagem);

        return "Notification processed: " + mensagem;
    }
}
