package com.renan.helloworld.controller;

import com.renan.helloworld.model.Endereco;
import com.renan.helloworld.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cep")
@Tag(name = "CEP SQS DYNAMODB", description = "Envio de CEP para fila SQS e consulta no DynamoDB")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @Operation(
        summary = "Envia CEP para a fila SQS",
        description = "Coloca o CEP na fila fila-cep do SQS para processamento assíncrono"
    )
    @PostMapping("/{cep}")
    public ResponseEntity<Map<String, String>> enviar(@PathVariable String cep) {
        cepService.enviarCep(cep);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("cep", cep);
        resposta.put("status", "NA_FILA");

        return ResponseEntity.accepted().body(resposta);
    }

    @Operation(
        summary = "Processa a fila SQS",
        description = "Consome os CEPs da fila, consulta o ViaCEP e salva os endereços no DynamoDB"
    )
    @PostMapping("/processar")
    public List<String> processar() {
        return cepService.processarFila();
    }

    @Operation(
        summary = "Busca endereço por CEP",
        description = "Lê o endereço salvo no DynamoDB pelo CEP informado"
    )
    @GetMapping("/{cep}")
    public Endereco buscar(@PathVariable String cep) {
        return cepService.buscarEndereco(cep);
    }

    @Operation(
        summary = "Espia a fila SQS",
        description = "Mostra os CEPs que estão aguardando na fila sem processar"
    )
    @GetMapping("/peek")
    public List<String> peek() {
        return cepService.peek();
    }

    @Operation(
        summary = "Deleta endereço por CEP",
        description = "Remove o endereço salvo no DynamoDB pelo CEP informado"
    )
    @DeleteMapping("/{cep}")
    public ResponseEntity<Void> deletar(@PathVariable String cep) {
        cepService.deletarEndereco(cep);
        return ResponseEntity.noContent().build();
    }
}
