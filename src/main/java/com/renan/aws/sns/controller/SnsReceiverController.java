package com.renan.aws.sns.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renan.aws.sns.service.SnsReceiverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sns/receiver")
@Tag(name = "SNS Receiver", description = "Recebe notificacoes do topico SNS via HTTP")
public class SnsReceiverController {

    private final SnsReceiverService snsReceiverService;

    // ObjectMapper converte o JSON que o SNS manda para um objeto que conseguimos ler
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SnsReceiverController(SnsReceiverService snsReceiverService) {
        this.snsReceiverService = snsReceiverService;
    }

    // o SNS chama este endpoint automaticamente via POST
    // ele pode mandar dois tipos de mensagem:
    //   SubscriptionConfirmation: quando registramos o endpoint, precisa confirmar
    //   Notification: mensagem real publicada no topico
    @Operation(summary = "Endpoint receptor do SNS", description = "O SNS chama este endpoint automaticamente. Confirma inscricao ou processa notificacao dependendo do tipo")
    @PostMapping
    public ResponseEntity<String> receber(@RequestBody String body) throws Exception {
        System.out.println("Requisicao recebida do SNS: " + body);

        // transforma o JSON recebido em um objeto navegavel
        JsonNode payload = objectMapper.readTree(body);

        // o campo "Type" diz o que o SNS quer que a gente faca
        String tipo = payload.get("Type").asText();

        System.out.println("Tipo da mensagem SNS: " + tipo);

        // primeira vez que o SNS chama o endpoint — precisa confirmar a inscricao
        // sem confirmar, as Notifications nao chegam
        if ("SubscriptionConfirmation".equals(tipo)) {
            return ResponseEntity.ok(snsReceiverService.confirmarInscricao(payload));
        }

        // mensagem real publicada no topico — aqui entra a logica de negocio
        if ("Notification".equals(tipo)) {
            return ResponseEntity.ok(snsReceiverService.processarNotificacao(payload));
        }

        System.out.println("Tipo SNS desconhecido: " + tipo);
        return ResponseEntity.ok("Tipo desconhecido: " + tipo);
    }
}
