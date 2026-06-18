package com.renan.aws.sns.controller;

import com.renan.aws.sns.service.SnsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sns")
@Tag(name = "SNS Publicador", description = "Publica mensagens e gerencia inscricoes no topico SNS")
public class SnsPublicadorController {

    private final SnsService snsService;

    public SnsPublicadorController(SnsService snsService) {
        this.snsService = snsService;
    }

    // publica uma mensagem no topico SNS
    // o SNS entrega automaticamente para todos os assinantes cadastrados
    // assinantes podem ser: email, http, sqs, lambda
    @Operation(summary = "Publica mensagem no SNS", description = "Envia mensagem para o topico — todos os assinantes recebem ao mesmo tempo")
    @PostMapping("/publicar")
    public ResponseEntity<String> publicar(@RequestParam String mensagem) {
        return ResponseEntity.ok(snsService.publicar(mensagem));
    }

    // inscreve um email no topico
    // o SNS manda um email com link de confirmacao — so recebe mensagens apos confirmar
    @Operation(summary = "Inscreve email no topico", description = "Assina um email no topico — o SNS envia email pedindo confirmacao")
    @PostMapping("/inscrever/email")
    public ResponseEntity<String> inscreverEmail(@RequestParam String email) {
        return ResponseEntity.ok(snsService.inscreverEmail(email));
    }

    // inscreve um endpoint HTTP no topico
    // o SNS vai chamar esse endpoint via POST sempre que uma mensagem for publicada
    // o endpoint precisa responder a SubscriptionConfirmation primeiro
    @Operation(summary = "Inscreve endpoint HTTP no topico", description = "Assina um endpoint HTTP — o SNS vai chamar esse endpoint quando houver mensagem")
    @PostMapping("/inscrever/http")
    public ResponseEntity<String> inscreverHttp(@RequestParam String endpointUrl) {
        return ResponseEntity.ok(snsService.inscreverHttp(endpointUrl));
    }
}
