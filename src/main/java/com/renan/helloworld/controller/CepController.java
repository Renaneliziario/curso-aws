package com.renan.helloworld.controller;

import com.renan.helloworld.model.Endereco;
import com.renan.helloworld.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cep")
@Tag(name = "CEP", description = "Envio de CEP para fila SQS e consulta no DynamoDB")
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
    public ResponseEntity<String> enviar(@PathVariable String cep) {
        return ResponseEntity.accepted().body(cepService.enviarCep(cep));
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
}
